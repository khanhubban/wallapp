package wallapp.account

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import wallapp.account.data.AccountDataManager
import wallapp.account.data.AccountDataRepository
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.firebase.FirebaseAuthUser
import wallapp.auth.firebase.userSignedIn
import wallapp.coroutine.collectIn
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.log.Log
import wallapp.network.NetworkState
import wallapp.preferences.UserPreferences
import wallapp.result.Result
import wallapp.result.ResultEx
import wallapp.result.dataOrNull
import wallapp.result.succeeded
import wallapp.signin.FirebaseAccountAlreadyLinkedException
import wallapp.signin.SignInProvider
import wallapp.signin.SignInProviderController
import wallapp.signin.SignInProviderCredentials
import wallapp.time.TimeRepository
import wallapp.userprofile.LoginType
import wallapp.userprofile.UserProfileException
import wallapp.userprofile.UserProfileFlag
import wallapp.userprofile.UserProfileFlags.hasSpecialCaseIsDeveloper
import wallapp.userprofile.UserProfileFlags.hasSpecialCasePlusEntitlement
import wallapp.userprofile.UserProfileLocal
import wallapp.userprofile.UserProfileRepository
import wallapp.userprofile.toSignInProvider

class AccountManagerDefault(
    private val firebaseAuthManager: FirebaseAuthManager,
    private val accountDataManager: AccountDataManager,
    private val accountDataRepository: AccountDataRepository,
    private val userPreferences: UserPreferences,
    private val userProfileRepository: UserProfileRepository,
    private val signInProviderController: SignInProviderController,
    private val timeRepository: TimeRepository,
    private val networkState: NetworkState,
    coroutineScopeMain: CoroutineScope,
    coroutineScopeIo: CoroutineScope,
) : AccountManager {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    private val firebaseAuthUser: StateFlow<FirebaseAuthUser?>
        get() = firebaseAuthManager.firebaseAuthUser

    private val FirebaseAuthUser.nonAnonymousUserId: String?
        get() = if (isAnonymous) null else userId

    override val signedInOrAnonymousUserId: StateFlow<String?> = firebaseAuthUser
        .map { it?.userId }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = firebaseAuthUser.value?.userId,
        )

    override val signedInUserId: StateFlow<String?> = firebaseAuthUser
        .map { it?.nonAnonymousUserId }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = firebaseAuthUser.value?.nonAnonymousUserId,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val signedInAccount: StateFlow<Account?> = firebaseAuthUser
        .mapLatest { firebaseAuthUser ->
            mapFirebaseAuthUserToAccount(firebaseAuthUser)
        }
        .onEach {
            Log.d("[firebase] signedInAccount: $it")
        }
        .stateIn(
            coroutineScopeMain,
            SharingStarted.Eagerly,
            null
        )

    private suspend fun mapFirebaseAuthUserToAccount(firebaseAuthUser: FirebaseAuthUser?): Account? {
        Log.d("[firebase] mapFirebaseAuthUserToAccount firebaseAuthUser received: $firebaseAuthUser")
        return if (firebaseAuthUser == null || firebaseAuthUser.isAnonymous) {
            null
        } else {
            val profile = userProfileRepository.currentUserProfile
                .onEach { Log.d("[firebase] userProfile for account: $it") }
                .filter {
                    it.succeeded && it.dataOrNull?.userId == firebaseAuthUser.userId
                }
                .first()
                .dataOrNull

            val flags = UserProfileFlag.fromExportStrings(profile?.flags)
            val hasSpecialCasePlusEntitlement = flags
                ?.any { hasSpecialCasePlusEntitlement(it) } ?: false

            val hasSpecialCaseIsDeveloper = flags
                ?.any { hasSpecialCaseIsDeveloper(it) } ?: false

            Log.d("[firebase] userProfile for account: $profile")
            Log.d("[firebase] userProfile for hasSpecialCasePlusEntitlement: $hasSpecialCasePlusEntitlement")
            Log.d("[firebase] userProfile for hasSpecialCaseIsDeveloper: $hasSpecialCaseIsDeveloper")

            firebaseAuthUser.toAccount(
                epochCreated = profile!!.epochCreated,
                hasSpecialCasePlusEntitlement = hasSpecialCasePlusEntitlement,
                hasSpecialCaseIsDeveloper = hasSpecialCaseIsDeveloper,
            )
        }
    }

    val isSignedInAnonymously: Boolean
        get() = firebaseAuthUser.value?.isAnonymous ?: false

    private val mutex = Mutex()

    override suspend fun signIn(signInMethod: SignInMethod): ResultEx<Unit> {
        mutex.withLock {
            return signInInternal(signInMethod)
        }
    }

    private suspend fun signInInternal(signInMethod: SignInMethod): ResultEx<Unit> {
        Log.d("[firebase] signing in with method: $signInMethod, userSignedIn: ${firebaseAuthManager.userSignedIn}")
        if (!checkForHalfSignedInStateAndResolve()) {
            return ResultEx.Error(AccountError.FirebaseSignInError(Exception("Failed to first sign out user")))
        }

        val loginType = signInMethod.toLoginType()
        val result = when (signInMethod) {
            SignInMethod.Apple -> signInViaProvider(SignInProvider.Apple, loginType)
            SignInMethod.Google -> signInViaProvider(SignInProvider.Google, loginType)
            SignInMethod.Anonymous -> signInAnonymously()
            SignInMethod.Email -> signInViaProvider(SignInProvider.Email, loginType)
        }
        return when (result) {
            is ResultEx.Success -> {
                createOrUpdateUserProfile(result.data, loginType)
            }

            is ResultEx.Error -> {
                ResultEx.Error(result.exception)
            }
        }
    }

    /**
     * First check if user is already signed in via FirebaseAuthManager but doesn't have a user profile
     * In that case, sign out the user and proceed with the sign in process
     */
    private suspend fun checkForHalfSignedInStateAndResolve(): Boolean {
        if (firebaseAuthManager.userSignedIn) {
            val currentUser = firebaseAuthUser.value
            val userProfile = userProfileRepository.currentUserProfile.first { it !is Result.Loading }
            Log.d("[firebase] check for sign out, currentUser: $currentUser, userProfile: $userProfile")
            // Only sign out if user profile doesn't exist or is invalid
            if (currentUser != null && userProfile is Result.Error && userProfile.exception::class in listOf(
                    UserProfileException.UserProfileInvalidException::class,
                    UserProfileException.UserNotFoundException::class,
                )) {
                return signOutCompletelyInternal()
            }
        }
        return true
    }

    private suspend fun signInAnonymously(): ResultEx<FirebaseAuthUser> {
        if (firebaseAuthManager.userSignedIn) {
            return ResultEx.Error(AccountError.UserAlreadySignedInError)
        }
        return when (val signInResult = firebaseAuthManager.signInAnonymously()) {
            is ResultEx.Error -> {
                ResultEx.Error(AccountError.AnonymousSignInError(signInResult.exception))
            }
            is ResultEx.Success -> ResultEx.Success(signInResult.data)
        }
    }

    private suspend fun signInViaProvider(
        signInProvider: SignInProvider,
        loginType: LoginType
    ): ResultEx<FirebaseAuthUser> {
        val currentUser = firebaseAuthUser.value
        if (currentUser != null && !currentUser.isAnonymous) {
            return ResultEx.Error(AccountError.UserAlreadySignedInError)
        }
        Log.d("[firebase] signing in with provider: $signInProvider")
        return when (val result = signInProviderController.performProviderSignIn(signInProvider)) {
            is ResultEx.Error -> {
                ResultEx.Error(AccountError.ProviderSignInError(result.exception))
            }
            is ResultEx.Success -> {
                handleSignInProviderCredential(result.data, currentUser, loginType)
            }
        }
    }

    private suspend fun handleSignInProviderCredential(
        credentials: SignInProviderCredentials,
        currentUser: FirebaseAuthUser?,
        loginType: LoginType
    ): ResultEx<FirebaseAuthUser> {
        // Check if currentUser is Anonymous and it has been deleted from Firebase auth
        // Note: This is just a check for sanity, as this should never happen because the user cannot delete an anonymous account
        var linkCredentials = currentUser != null
        if (currentUser != null && currentUser.isAnonymous) {
            val authToken = firebaseAuthManager.getRefreshedAuthTokenForCurrentUser()
            linkCredentials = authToken != null
        }

        if (!linkCredentials) {
            return signInViaCredentials(credentials)
        }
        try {
            return when (val linkingResult = firebaseAuthManager.linkWithCredential(credentials)) {
                is ResultEx.Success -> {
                    Log.d("[firebase] Successfully linked credentials")
                    val updatedProfile = updatedUserProfile(linkingResult.data, loginType) ?: run {
                        signOutInternal()
                        return ResultEx.Error(AccountError.GetUserProfileError(linkingResult.data.userId))
                    }
                    userProfileRepository.updateUserProfile(updatedProfile)
                    ResultEx.Success(linkingResult.data)
                }

                is ResultEx.Error -> {
                    val exception = linkingResult.exception
                    Log.w(exception, "[firebase] ${exception.message}")
                    if (exception is FirebaseAccountAlreadyLinkedException) {
                        signInViaCredentials(credentials)
                    } else {
                        ResultEx.Error(AccountError.FirebaseSignInError(exception))
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(ex, "[firebase] failed to link credentials")
            if (ex is UserProfileException.UserProfileUpdateException) {
                signOutInternal()
                return ResultEx.Error(AccountError.UpdateUserProfileError(ex))
            }
            return ResultEx.Error(AccountError.FirebaseSignInError(ex))
        }
    }

    private suspend fun updatedUserProfile(firebaseAuthUser: FirebaseAuthUser, loginType: LoginType): UserProfileLocal? {
        val userProfile = userProfileRepository.getUserProfile(firebaseAuthUser.userId) ?: return null
        return userProfile.copy(
            email = firebaseAuthUser.email,
            isAnonymous = firebaseAuthUser.isAnonymous,
            loginType = loginType,
            epochLastUpdated = timeRepository.currentTime,
        )
    }

    private suspend fun signInViaCredentials(credentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> =
        when (val signInResult = firebaseAuthManager.signIn(credentials)) {
            is ResultEx.Error -> {
                ResultEx.Error(AccountError.FirebaseSignInError(signInResult.exception))
            }
            is ResultEx.Success -> {
                ResultEx.Success(signInResult.data)
            }
        }

    override suspend fun signOut(): Boolean {
        mutex.withLock {
            return signOutInternal()
        }
    }

    private suspend fun signOutInternal(): Boolean {
        try {
            Log.d("[firebase] signing out")
            val currentUser = firebaseAuthUser.value ?: return false
            firebaseAuthManager.signOut()
            Log.d("[firebase] signed out")
            val signInProvider =
                userProfileRepository.currentUserProfile.value.dataOrNull?.loginType?.toSignInProvider()
            signInProviderController.performProviderSignOut(signInProvider)
            return true
        } catch (ex: Exception) {
            // A note on this error handling: This would not happen in the normal flow of events,
            // as sign out call finishes even when on airplane mode.
            // This is a catch-all for any unexpected errors that might occur during sign out.
            // One such instance is the coroutine scope being canceled in which this function is called.
            Log.e(ex, "[firebase] failed to sign out")
            crashTracking.logNonFatalException(
                NonFatalException(
                    message = "Failed to sign out, ui: ${firebaseAuthUser.value?.userId}",
                    cause = ex
                )
            )
            return false
        }
    }

    override suspend fun deleteAccount(): ResultEx<Unit> {
        mutex.withLock {
            val currentUser = firebaseAuthUser.value
            if (currentUser == null || currentUser.isAnonymous) {
                return ResultEx.Error(AccountError.UserNotSignedInError)
            }
            val signInProvider =
                userProfileRepository.currentUserProfile.value.dataOrNull?.loginType?.toSignInProvider()
                    ?: return ResultEx.Error(AccountError.UserNotSignedInError)

            Log.d("[firebase] re-authenticating : signInProvider=$signInProvider")
            val reAuthenticationResult =
                signInProviderController.performProviderSignIn(signInProvider)
            if (reAuthenticationResult is ResultEx.Error) {
                return ResultEx.Error(AccountError.ProviderSignInError(reAuthenticationResult.exception))
            }

            if (!networkState.isConnected) {
                return ResultEx.Error(AccountError.NetworkConnectionError)
            }

            try {
                Log.d("[firebase] deleting user data")
                withTimeout(5000L) {
                    accountDataManager.deletePIIData()
                }
            } catch(ex: TimeoutCancellationException) {
                Log.e(ex, "[firebase] user data deletion timed out")
                // This can happen if the delete call times out due to network issues
                // we can continue deleting account as deletion is queued and will be processed
                // when internet is available
            } catch (ex: Exception) {
                Log.e(ex, "[firebase] failed to delete user data")
                crashTracking.logNonFatalException(
                    NonFatalException(
                        message = "Failed to delete user data, ui: ${currentUser.userId}",
                        cause = ex
                    )
                )
                return ResultEx.Error(AccountError.DeleteAccountError(ex))
            }

            Log.d("[firebase] deleting user account")
            val deleteUserResult = firebaseAuthManager.deleteUser((reAuthenticationResult as ResultEx.Success).data)
            if (deleteUserResult is ResultEx.Error) {
                return ResultEx.Error(AccountError.FirebaseDeleteUserError(exception = deleteUserResult.exception))
            }

            Log.d("[firebase] signing out from provider")
            signInProviderController.performProviderSignOut(signInProvider)
            return ResultEx.Success(Unit)
        }
    }

    override fun updateFirebaseCloudMessagingToken(token: String) {
        if (userPreferences.firebaseCloudMessagingToken.value != token) {
            userPreferences.firebaseCloudMessagingToken.value = token
        }
    }

    private suspend fun createOrUpdateUserProfile(
        firebaseAuthUser: FirebaseAuthUser,
        loginType: LoginType,
    ): ResultEx<Unit> {
        try {
            if (!userProfileRepository.userProfileExists(firebaseAuthUser.userId)) {
                Log.d("[firestore] Creating user profile after sign in: $firebaseAuthUser")
                userProfileRepository.createUserProfile(firebaseAuthUser, loginType)
//                if (!accountDataManager.syncLocalDataToRemote()) {
//                    throw AccountError.UpdateUserProfileError(Exception("Failed to sync local data to remote"))
//                }
            } else {
                Log.d("[firestore] User profile already exists, updating user profile after sign in: $firebaseAuthUser")
                val userProfile = userProfileRepository.getUserProfile(firebaseAuthUser.userId)
                    ?: throw AccountError.GetUserProfileError(firebaseAuthUser.userId)
                val updatedUserProfile = userProfile.copy(
                    epochLastSeen = timeRepository.currentTime,
                    loginType = loginType,
                )
                Log.d("[firestore] Updating user profile after sign in: $updatedUserProfile")
                userProfileRepository.updateUserProfile(updatedUserProfile)
                accountDataManager.deleteLocalData()
            }
            return ResultEx.Success(Unit)
        } catch (ex: AccountError.GetUserProfileError) {
            Log.e("[firestore] failed to get user profile. exception: ${ex.message}")
            crashTracking.logNonFatalException(
                NonFatalException(
                    message = "Failed to get user profile, ui: ${firebaseAuthUser.userId}",
                    cause = ex
                )
            )
            signOutInternal() // Reverse the sign in process since it was not completed due to user profile issue
            return ResultEx.Error(ex)
        } catch (ex: Exception) {
            // This can happen if the job that is calling this function is cancelled
            Log.e("[firestore] failed to create or update user profile. exception: ${ex.message}")
            crashTracking.logNonFatalException(
                NonFatalException(
                    message = "Failed to create or update user profile, ui: ${firebaseAuthUser.userId}",
                    cause = ex
                )
            )
            signOutInternal() // Reverse the sign in process since it was not completed due to user profile issue
            return ResultEx.Error(AccountError.CreateUserProfileError(exception = ex))
        }
    }

    override suspend fun signOutCompletely(): Boolean {
        mutex.withLock {
            return signOutCompletelyInternal()
        }
    }

    private suspend fun signOutCompletelyInternal(): Boolean {
        if (signOutInternal()) {
            // wait for sign out to complete
            val result = withTimeoutOrNull(2000L) { // not needed but extra precaution so the flow doesn't get stuck
                firebaseAuthUser.first { it == null }
            }
            return result != null
        }
        return false
    }

    init {
        // Even though the syncing of metadata should happen for the following flows independently,
        // we are combining them here for 2 reasons:
        // 1. To avoid multiple calls to syncMetadataToRemote() initially
        // 2. To make sure that dataRefreshed is not triggering sync unless userProfile is available
        combine(
            userProfileRepository.currentUserProfile
                .filterIsInstance<Result.Success<UserProfileLocal>>()
                .map { it.data.userId }
                .distinctUntilChanged(),
            accountDataManager.dataRefreshed,
        ) { _, _ ->
            Log.d("[firestore] [DeviceRecords] AccountManagerDefault: userProfile and dataRefreshed updated")
        }.collectIn(coroutineScopeMain) {
            accountDataManager.syncMetadataToRemote()
        }

        // Collect user profile and if UserNotFoundException is thrown, sign out the user
        // as the user could have deleted their account from another device
        userProfileRepository.currentUserProfile
            .filterIsInstance<Result.Error>()
            .filter { it.exception is UserProfileException.UserNotFoundException || it.exception is UserProfileException.UserProfileInvalidException }
            .onEach { Log.d("[firestore] ${it.exception} thrown, checking for sign out") }
            .collectIn(coroutineScopeIo) {
                mutex.withLock {
                    // check if user is still signed in and userProfile is still not available
                    val currentUserProfile = userProfileRepository.currentUserProfile.value
                    if (firebaseAuthManager.userSignedIn && currentUserProfile is Result.Error &&
                        (currentUserProfile.exception is UserProfileException.UserNotFoundException ||
                                currentUserProfile.exception is UserProfileException.UserProfileInvalidException)) {
                        Log.d("[firestore] UserNotFoundException or UserProfileInvalidException thrown, signing out")
                        if (signOutCompletelyInternal()) {
                            signInInternal(SignInMethod.Anonymous)
                        }
                    }
                }
            }
    }
}
