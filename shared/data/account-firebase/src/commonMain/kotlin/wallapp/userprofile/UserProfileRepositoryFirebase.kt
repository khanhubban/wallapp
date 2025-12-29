package wallapp.userprofile

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Source
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.firebase.FirebaseAuthUser
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.devicerecord.normalizeDeviceRecordsExportString
import wallapp.firebase.FirestoreConstants
import wallapp.log.Log
import wallapp.preferences.UserPreferences
import wallapp.result.Result
import wallapp.result.dataOrNull
import wallapp.time.TimeRepository
import wallapp.userprofile.UserProfileRepositoryArbitrator.computePersistedEmail
import wallapp.userprofile.UserProfileRepositoryArbitrator.sanitizeEmailFields

/**
 * The catch blocks in this class catch exceptions that occur out of the ordinary flow of events.
 * Updating user even when offline is completely fine as we use Firestore's offline capabilities.
 * But there are instances where the user might be deleted from Firestore and we try to update/get
 * the user profile. This will throw a permission denied or not found exception. We catch those here.
 * Nothing else to be done as the user will be signed out elsewhere.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileRepositoryFirebase(
    private val timeRepository: TimeRepository,
    private val userPreferences: UserPreferences,
    private val firebaseAuthManager: FirebaseAuthManager,
    coroutineScopeMain: CoroutineScope
) : UserProfileRepository {

    private val enableLogging = true

    // Important: Supports only one collector
    private val _userProfileErrorFlow = MutableSharedFlow<UserProfileException>(extraBufferCapacity = 1)
    override val userProfileErrorFlow: Flow<UserProfileException>
        get() = _userProfileErrorFlow.asSharedFlow()

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    private val firebaseFirestore: FirebaseFirestore
        get() = Firebase.firestore

    override val firebaseAuthUserId: String?
        get() = firebaseAuthManager.firebaseAuthUser.value?.userId

    override val currentUserProfile: StateFlow<Result<UserProfileLocal>> = firebaseAuthManager
        .firebaseAuthUser
        .flatMapLatest { user ->
            Log.d("[firestore] currentUserProfile.flatMapLatest: $user")
            firebaseFirestore.collection(FirestoreConstants.Collection.Users)
                .document(
                    user?.userId ?: return@flatMapLatest flowOf(
                        Result.Error(
                            UserProfileException.UserNotSignedInException()
                        )
                    )
                )
                .snapshots
                .onEach { snapshot ->
                    Log.d("[firestore] currentUserProfile.onEach snapshot: $snapshot")
                }
                .map { snapshot ->
                    if (snapshot.exists) {
                        val profile = snapshot.data<UserProfileRemote>().toUserProfileLocal()
                            ?: return@map Result.Error(UserProfileException.UserProfileInvalidException())
                        if (profile.accountDeleted) {
                            return@map Result.Error(UserProfileException.UserNotFoundException())
                        }
                        Result.Success(profile)
                    } else {
                        Result.Error(UserProfileException.UserNotFoundException())
                    }
                }
                .catch { e ->
                    // If user is deleted from Firestore, this will be called with a permission denied exception
                    // because we will sign out the user via FirebaseAuthManager and the request to Firestore
                    // will reach with a null authId trying to read document of the deleted user. Hence it will
                    // short-circuit the request as authId will not match the document/user id.
                    Log.e("[firestore] currentUserProfile.flatMapLatest.catch: $e")
                    crashTracking.logNonFatalThrowable(
                        NonFatalException(
                            "cUP.flatMap.catch, ui: ${user.userId}, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                            e
                        )
                    )
                    emit(Result.Error(UserProfileException.UserProfileFetchException(e)))
                }
        }.catch {
            Log.e("[firestore] currentUserProfile.catch: $it")
            emit(Result.Error(UserProfileException.UserProfileFetchException(it)))
        }.onEach {
            Log.d("[firestore] userProfileResult.onEach: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = Result.Loading,
        )

    private val userId: String?
        get() = currentUserProfile.value.dataOrNull?.userId

    override suspend fun createUserProfile(firebaseAuthUser: FirebaseAuthUser, loginType: LoginType) {
        log("[firestore] createUserProfile()")
        val db = firebaseFirestore
        // Persist email only when the user opted into newsletters and has a non-anonymous auth email.
        val persistedEmail = computePersistedEmail(
            authEmail = firebaseAuthUser.email,
            newsletter = userPreferences.joinNewsletter.value,
            isAnonymous = firebaseAuthUser.isAnonymous
        )
        val userProfile = UserProfileRemote(
            userId = firebaseAuthUser.userId,
            email = persistedEmail,
            isAnonymous = firebaseAuthUser.isAnonymous,
            loginType = loginType.label,
            epochCreated = timeRepository.currentTime,
            epochLastUpdated = timeRepository.currentTime,
            epochLastSeen = timeRepository.currentTime,
            favoriteIds = emptyList(),
            currentWallpaperIds = mapOf(),
            followingIds = emptyList(),
            purchaseRecords = emptyList(),
            newsletter = userPreferences.joinNewsletter.value,
            receiveNotifications = userPreferences.receiveNotifications.value,
        )
        try {
            db.collection(FirestoreConstants.Collection.Users)
                .document(firebaseAuthUser.userId)
                .set(userProfile)
        } catch (e: Exception) {
            Log.e("[firestore] createUserProfile() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "cUP, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            throw UserProfileException.UserProfileCreateException(e, e.message)
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfileLocal) {
        try {
            log("[firestore] updateUserProfile() : $userProfile")
            val db = firebaseFirestore
            val userRef = db.collection(FirestoreConstants.Collection.Users)
                .document(userProfile.userId)
            val userSnapshot = userRef.get()
            if (!userSnapshot.exists) return
            val currentTime = timeRepository.currentTime
            val fieldsAndValues = mutableListOf<Pair<String, Any?>>()
            val authUser = firebaseAuthManager.firebaseAuthUser.value
            // Email must follow the same rule set as creation (opt-in + non-anon + auth email).
            val persistedEmail = computePersistedEmail(
                authEmail = authUser?.email,
                newsletter = userProfile.newsletter,
                isAnonymous = userProfile.isAnonymous
            )
            fieldsAndValues.addAll(
                listOf(
                FirestoreConstants.Field.Email to persistedEmail,
                "isAnonymous" to userProfile.isAnonymous,
                FirestoreConstants.Field.LoginType to userProfile.loginType.label,
                FirestoreConstants.Field.FavoriteIds to userProfile.favoriteIds,
                FirestoreConstants.Field.CurrentWallpaperIds to userProfile.currentWallpaperIds,
                FirestoreConstants.Field.FollowingIds to userProfile.followingIds,
                FirestoreConstants.Field.PurchaseRecords to userProfile.purchaseRecords,
                FirestoreConstants.Field.Currency to userProfile.currency,
                FirestoreConstants.Field.Newsletter to userProfile.newsletter,
                FirestoreConstants.Field.Flags to userProfile.flags,
                FirestoreConstants.Field.WallpaperDownloadEvents to userProfile.wallpaperDownloadEvents,
                FirestoreConstants.Field.ReceiveNotifications to userProfile.receiveNotifications,
                FirestoreConstants.Field.AccountDeleted to userProfile.accountDeleted,
                FirestoreConstants.Field.EpochLastUpdated to currentTime,
                FirestoreConstants.Field.EpochLastSeen to currentTime,
                )
            )
            val deviceInfo = userProfile.deviceInfo
            if (deviceInfo == null || deviceInfo.isBlank()) {
                fieldsAndValues.add(FirestoreConstants.Field.DeviceInfo to FieldValue.delete)
            } else {
                val normalizedDeviceInfo = normalizeDeviceRecordsExportString(deviceInfo)
                if (normalizedDeviceInfo != null) {
                    fieldsAndValues.add(FirestoreConstants.Field.DeviceInfo to normalizedDeviceInfo)
                } else {
                    log("[firestore] updateUserProfile() - deviceInfo normalization failed; skipping DeviceInfo update")
                }
            }
            userRef.update(*fieldsAndValues.toTypedArray())
        } catch (e: Exception) {
            Log.e("[firestore] updateUserProfile() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "uUP, ui: ${userProfile.userId}, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            throw UserProfileException.UserProfileUpdateException(e, e.message)
        }
    }

    override suspend fun userProfileExists(userId: String): Boolean {
        try {
            log("[firestore] userProfileExists(), ui: $userId")
            val db = firebaseFirestore
            val userRef = db.collection(FirestoreConstants.Collection.Users)
                .document(userId)
                .get()
            return userRef.exists
        } catch (e: Exception) {
            Log.e("[firestore] userProfileExists() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "uPE, ui: $userId, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            return false
        }
    }

    override suspend fun getUserProfile(userId: String): UserProfileLocal? {
        try {
            log("[firestore] getUserProfile()")
            val db = firebaseFirestore
            val userSnapshot = db.collection(FirestoreConstants.Collection.Users)
                .document(userId)
                .get()
            return if (userSnapshot.exists) {
                userSnapshot.data<UserProfileRemote>().toUserProfileLocal()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("[firestore] getUserProfile() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "gUP, ui: $userId, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            return null
        }
    }

    override suspend fun getUserProfileFromServer(userId: String): UserProfileLocal? {
        try {
            log("[firestore] getUserProfileFromServer()")
            val db = firebaseFirestore
            val userSnapshot = db.collection(FirestoreConstants.Collection.Users)
                .document(userId)
                .get(source = Source.SERVER)
            return if (userSnapshot.exists) {
                userSnapshot.data<UserProfileRemote>().toUserProfileLocal()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("[firestore] getUserProfileFromServer() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "gUPFS, ui: $userId, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            return null
        }
    }

    override suspend fun updateUserProfileFields(
        vararg fieldsAndValues: Pair<String, Any?>,
        canPostError: Boolean
    ): Boolean {
        try {
            log("[firestore] updateUserProfileFields()")
            val userId = userId ?: throw UserProfileException.UserNotFoundException()
            val db = firebaseFirestore
            val userRef = db.collection(FirestoreConstants.Collection.Users)
                .document(userId)
            val currentTime = timeRepository.currentTime
            // Enforce email persistence rules whenever email/newsletter/anonymous fields are updated.
            val currentProfile = currentUserProfile.value.dataOrNull
            val authUser = firebaseAuthManager.firebaseAuthUser.value
            val sanitizedFields = sanitizeEmailFields(
                fieldsAndValues = fieldsAndValues.toList(),
                emailField = FirestoreConstants.Field.Email,
                newsletterField = FirestoreConstants.Field.Newsletter,
                isAnonymousField = "isAnonymous",
                authEmail = authUser?.email,
                currentNewsletter = currentProfile?.newsletter ?: userPreferences.joinNewsletter.value,
                currentIsAnonymous = currentProfile?.isAnonymous ?: false
            ).toMutableList()
            sanitizedFields.removeAll {
                it.first == FirestoreConstants.Field.EpochLastUpdated ||
                    it.first == FirestoreConstants.Field.EpochLastSeen
            }
            userRef.update(
                *sanitizedFields.toTypedArray(),
                FirestoreConstants.Field.EpochLastUpdated to currentTime,
                FirestoreConstants.Field.EpochLastSeen to currentTime
            )
            return true
        } catch (e: Exception) {
            Log.e("[firestore] updateUserProfileFields() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "uUPF, ui: $userId, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            if (canPostError) {
                // Only need to post error here because other functions have error handling at the calling site
                // This function is used for any other kinds of updates to the user profile
                _userProfileErrorFlow.tryEmit(UserProfileException.UserProfileUpdateException(e, e.message))
            }
            return false
        }
    }

    override suspend fun deletePIIFields() {
        try {
            log("[firestore] deletePIIFields()")
            val userId = userId ?: throw UserProfileException.UserNotFoundException()
            val db = firebaseFirestore
            val userRef = db.collection(FirestoreConstants.Collection.Users)
                .document(userId)
            userRef.update(
                FirestoreConstants.Field.Email to null,
                FirestoreConstants.Field.DeviceInfo to null,
                FirestoreConstants.Field.Currency to null,
                FirestoreConstants.Field.Newsletter to null,
                FirestoreConstants.Field.Flags to null,
                FirestoreConstants.Field.ReceiveNotifications to null,
                FirestoreConstants.Field.LoginType to LoginType.None.label,
                FirestoreConstants.Field.EpochLastSeen to timeRepository.currentTime,
                FirestoreConstants.Field.EpochLastUpdated to timeRepository.currentTime,
                FirestoreConstants.Field.FavoriteIds to emptyList<String>(),
                FirestoreConstants.Field.CurrentWallpaperIds to emptyMap<String, String>(),
                FirestoreConstants.Field.FollowingIds to emptyList<String>(),
                FirestoreConstants.Field.PurchaseRecords to emptyList<String>(),
                FirestoreConstants.Field.AccountDeleted to true,
            )
        } catch (e: Exception) {
            Log.e("[firestore] deletePIIFields() : $e")
            crashTracking.logNonFatalThrowable(
                NonFatalException(
                    "dPF, ui: $userId, sui: ${firebaseAuthManager.firebaseAuthUser.value?.userId}",
                    e
                )
            )
            throw UserProfileException.UserProfileDeleteException(e, e.message)
        }
    }

    private fun log(message: String, vararg str: Any? = arrayOf<Any?>(null)) {
        if (enableLogging) {
            Log.d("[SPECIAL] " + message, *str)
        }
    }

}
