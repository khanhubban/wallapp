package wallapp.data.favorite

import dev.gitlive.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.account.data.AccountDataDefaults
import wallapp.account.data.AccountDataRepository
import wallapp.buildconfig.BuildConfig
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDownloadEvent
import wallapp.devicerecord.DeviceRecord
import wallapp.devicerecord.mapDeviceRecordsFromExportString
import wallapp.devicerecord.normalizeDeviceRecordsExportString
import wallapp.firebase.FirestoreConstants
import wallapp.log.Logger
import wallapp.process.Process
import wallapp.result.Result
import wallapp.result.dataOrNull
import wallapp.userprofile.UserProfileFlag
import wallapp.userprofile.UserProfileFlags.hasSpecialCaseIsDeveloper
import wallapp.userprofile.UserProfileFlags.hasSpecialCasePlusEntitlement
import wallapp.userprofile.UserProfileRepository


class AccountDataRepositoryFirebaseMobile(
    accountDataDefaults: AccountDataDefaults,
    private val userProfileRepository: UserProfileRepository,
    process: Process,
    private val buildConfig: BuildConfig,
    coroutineScopeMain: CoroutineScope,
) : AccountDataRepository {

    override val acceptedTerms: MutableStateFlow<Boolean>
        get() = throw IllegalStateException("Only accessible through local data repository")
    override val reportUsageStats: MutableStateFlow<Boolean>
        get() = throw IllegalStateException("Only accessible through local data repository")

    override val hasSpecialCasePlusEntitlement: StateFlow<Boolean?> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            val flags = UserProfileFlag.fromExportStrings(profile?.flags)
            flags?.any { hasSpecialCasePlusEntitlement(it) } ?: false
        }
        .onEach { Log.d("[firestore] hscpe.onEach: $it") }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    private val hasSpecialCaseIsDeveloperDefault: Boolean
        get() = buildConfig.debug

    override val hasSpecialCaseIsDeveloper: StateFlow<Boolean?> = userProfileRepository.currentUserProfile
        .onEach { Log.d("[firestore] hscid.onEach profileResult: $it") }
        .map { profileResult ->
            when (profileResult) {
                is Result.Success -> {
                    val flags = UserProfileFlag.fromExportStrings(profileResult.data.flags)
                    flags?.any { hasSpecialCaseIsDeveloper(it) } ?: hasSpecialCaseIsDeveloperDefault
                }
                is Result.Error,
                is Result.Loading -> {
                    if (hasSpecialCaseIsDeveloperDefault) { true } else { null }
                }
            }
        }
        .onEach { Log.d("[firestore] hscid.onEach: $it") }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = hasSpecialCaseIsDeveloperDefault,
        )

    override val favoriteIds: StateFlow<List<Id>?> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            profile?.favoriteIds?.map { Id.fromExportShortString(it) }
        }.onEach {
            Log.d("[firestore] favoriteIds.onEach: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
    override val followingIds: StateFlow<List<Id>?> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            profile?.followingIds?.map { Id.fromExportShortString(it) }
        }.onEach {
            Log.d("[firestore] followingIds.onEach: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
    override val purchaseRecords: StateFlow<List<Id>?> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            profile?.purchaseRecords?.map { Id.fromExportShortString(it) }
        }.onEach {
            Log.d("[firestore] purchasedIds.onEach: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
    override val receiveNewsletter: StateFlow<Boolean> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            profile?.newsletter ?: accountDataDefaults.receiveNewsletter
        }.onEach {
            Log.d("[firestore] newsletter: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = accountDataDefaults.receiveNewsletter,
        )
    override val deviceRecords: StateFlow<List<DeviceRecord>?> =
        userProfileRepository.currentUserProfile
            .map { it.dataOrNull }
            .map { profile ->
                mapDeviceRecordsFromExportString(profile?.deviceInfo ?: "")
            }.onEach {
                Log.d("[firestore] deviceInfo: $it")
            }.stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )
    override val wallpaperDownloadEvents: StateFlow<List<WallpaperDownloadEvent>?> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            profile?.wallpaperDownloadEvents?.map { WallpaperDownloadEvent.fromExportString(it) }
        }.onEach {
            Log.d("[firestore] wallpaperDownloadEvents.onEach: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    override val receiveNotifications: StateFlow<Boolean> = userProfileRepository.currentUserProfile
        .map { it.dataOrNull }
        .map { profile ->
            profile?.receiveNotifications ?: accountDataDefaults.receiveNotifications
        }.onEach {
            Log.d("[firestore] receiveNotifications: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = accountDataDefaults.receiveNotifications,
        )

    override suspend fun setIsFavorite(id: Id, isFavorite: Boolean): Boolean {
        log("[firestore] updateFavorite($id, $isFavorite)")
        val data = if (isFavorite) {
            FieldValue.arrayUnion(id.exportString)
        } else {
            FieldValue.arrayRemove(id.exportString)
        }
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.FavoriteIds to data)
        return true
    }

    override fun isFavorite(id: Id): Boolean {
        return favoriteIds.value?.contains(id) == true
    }

    override suspend fun setIsFollowing(id: Id, isFollowing: Boolean): Boolean {
        log("[firestore] updateFollowing($id, $isFollowing)")
        val data = if (isFollowing) {
            FieldValue.arrayUnion(id.exportString)
        } else {
            FieldValue.arrayRemove(id.exportString)
        }
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.FollowingIds to data)
        return true
    }

    override fun isFollowing(id: Id): Boolean {
        return followingIds.value?.contains(id) == true
    }

    override suspend fun setIsPurchased(id: Id, isPurchased: Boolean): Boolean {
        log("[firestore] updatePurchased($id, $isPurchased)")
        val data = if (isPurchased) {
            FieldValue.arrayUnion(id.exportString)
        } else {
            FieldValue.arrayRemove(id.exportString)
        }
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.PurchaseRecords to data)
        return true
    }


    override fun isPurchased(id: Id): Boolean {
        return purchaseRecords.value?.contains(id) == true
    }

    override suspend fun updateAllFavorites(ids: List<Id>) {
        log("[firestore] updateAllFavorite(${ids.size})")
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.FavoriteIds to
                FieldValue.arrayUnion(*ids.map { it.exportString }.toTypedArray()))
    }

    override suspend fun updateAllFollowing(ids: List<Id>) {
        log("[firestore] updateAllFollowing(${ids.size})")
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.FollowingIds to
                FieldValue.arrayUnion(*ids.map { it.exportString }.toTypedArray()))
    }

    override suspend fun updateAllPurchased(ids: List<Id>) {
        log("[firestore] updateAllPurchased(${ids.size})")
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.PurchaseRecords to
                FieldValue.arrayUnion(*ids.map { it.exportString }.toTypedArray()))
    }

    override suspend fun updateReceiveNewsletter(receiveNewsletter: Boolean) {
        log("[firestore] updateNewsletter(${receiveNewsletter})")
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.ReceiveNewsletter to receiveNewsletter)
    }

    override suspend fun updateDeviceInfo(deviceInfo: String) {
        if (deviceInfo.isBlank()) {
            log("[firestore] updateDeviceInfo(delete)")
            userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.DeviceInfo to FieldValue.delete)
            return
        }
        val normalizedDeviceInfo = normalizeDeviceRecordsExportString(deviceInfo)
        log("[firestore] updateDeviceInfo(normalized=${normalizedDeviceInfo != null})")
        if (normalizedDeviceInfo != null) {
            userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.DeviceInfo to normalizedDeviceInfo)
        } else {
            log("[firestore] updateDeviceInfo() - deviceInfo normalization failed; skipping DeviceInfo update")
        }
    }

    override suspend fun updateWallpaperDownloadEvent(wallpaperDownloadEvent: WallpaperDownloadEvent, isAdd: Boolean): Boolean {
        log("[firestore] updateWallpaperDownloadEvent($wallpaperDownloadEvent, $isAdd)")
        val data = if (isAdd) {
            FieldValue.arrayUnion(wallpaperDownloadEvent.exportString)
        } else {
            FieldValue.arrayRemove(wallpaperDownloadEvent.exportString)
        }
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.WallpaperDownloadEvents to data)
        return true
    }

    override suspend fun updateAllWallpaperDownloadEvent(wallpaperDownloadEvents: List<WallpaperDownloadEvent>) {
        log("[firestore] updateAllWallpaperDownloadEvent(${wallpaperDownloadEvents.size})")
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.WallpaperDownloadEvents to
                FieldValue.arrayUnion(*wallpaperDownloadEvents.map { it.exportString }.toTypedArray()))
    }

    override suspend fun updateReceiveNotifications(receiveNotifications: Boolean) {
        log("[firestore] updateReceiveNotifications(${receiveNotifications})")
        userProfileRepository.updateUserProfileFields(FirestoreConstants.Field.ReceiveNotifications to receiveNotifications)
    }

    override suspend fun deleteAll() {
        log("[firestore] deleteAll()")
        // Delete all Personal Identifiable Information (PII) fields
        userProfileRepository.deletePIIFields()
    }

    override suspend fun updateAll(
        favoriteIds: List<Id>?,
        followingIds: List<Id>?,
        purchaseRecords: List<Id>?,
        receiveNewsletter: Boolean?,
        deviceInfo: String?,
        wallpaperDownloadEvents: List<WallpaperDownloadEvent>?,
        receiveNotifications: Boolean?,
        canPostError: Boolean
    ): Boolean {
        log("[firestore] updateAll()")
        val fieldsAndValues = mutableListOf<Pair<String, Any?>>()
        favoriteIds?.let { ids ->
            fieldsAndValues.add(FirestoreConstants.Field.FavoriteIds to FieldValue.arrayUnion(*ids.map { it.exportString }.toTypedArray()))
        }
        followingIds?.let { ids ->
            fieldsAndValues.add(FirestoreConstants.Field.FollowingIds to FieldValue.arrayUnion(*ids.map { it.exportString }.toTypedArray()))
        }
        purchaseRecords?.let { ids ->
            fieldsAndValues.add(FirestoreConstants.Field.PurchaseRecords to FieldValue.arrayUnion(*ids.map { it.exportString }.toTypedArray()))
        }
        receiveNewsletter?.let { fieldsAndValues.add(FirestoreConstants.Field.ReceiveNewsletter to receiveNewsletter) }
        if (deviceInfo != null) {
            if (deviceInfo.isBlank()) {
                fieldsAndValues.add(FirestoreConstants.Field.DeviceInfo to FieldValue.delete)
            } else {
                val normalizedDeviceInfo = normalizeDeviceRecordsExportString(deviceInfo)
                if (normalizedDeviceInfo != null) {
                    fieldsAndValues.add(FirestoreConstants.Field.DeviceInfo to normalizedDeviceInfo)
                } else {
                    log("[firestore] updateAll() - deviceInfo normalization failed; skipping DeviceInfo update")
                }
            }
        }
        wallpaperDownloadEvents?.let { events ->
            fieldsAndValues.add(FirestoreConstants.Field.WallpaperDownloadEvents to FieldValue.arrayUnion(*events.map { it.exportString }.toTypedArray()))
        }
        receiveNotifications?.let { fieldsAndValues.add(FirestoreConstants.Field.ReceiveNotifications to receiveNotifications) }
        return userProfileRepository.updateUserProfileFields(*fieldsAndValues.toTypedArray(), canPostError = canPostError)
    }

    override fun getUserProfileId(): String? {
        return userProfileRepository.currentUserProfile.value.dataOrNull?.userId
    }

    override fun getFirebaseUserId(): String? {
        return userProfileRepository.firebaseAuthUserId
    }

    companion object {
        private val Log = Logger("[FirebaseSync] AccountDataRepositoryFirebaseMobile")

        //    private val enableLogging = true
        private fun log(message: String, vararg str: Any? = arrayOf<Any?>(null)) {
//        if (enableLogging) {
            Log.w(message, *str)
//        }
        }
    }

    init {
        require(process.isDefaultProcess) { "Firebase only supports operation from the default process." }
    }
}
