package wallapp.auth.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.auth.apple.AppleAuthManager
import wallapp.auth.apple.provideAppleAuthManager
import wallapp.auth.google.GoogleAuthData
import wallapp.buildconfig.BuildConfig
import wallapp.crashtracking.CrashTrackingHolder.crashTracking
import wallapp.log.Log
import wallapp.process.Process
import wallapp.result.ResultEx
import wallapp.signin.FirebaseAccountAlreadyLinkedException
import wallapp.signin.SignInProviderCredentials


class FirebaseAuthManagerFirebase(
    process: Process,
    coroutineScopeMain: CoroutineScope,
    private val buildConfig: BuildConfig,
) : FirebaseAuthManager {

    private val appleAuthManager: AppleAuthManager = provideAppleAuthManager()

    private val firebaseAuth: FirebaseAuth
        get() = Firebase.auth
    private val firebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    private val _firebaseAuthUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(firebaseUser)
    override val firebaseAuthUser: StateFlow<FirebaseAuthUser?> =
        merge(firebaseAuth.authStateChanged, _firebaseAuthUser)
            .map { it?.firebaseAuthUser }
            .onEach { Log.d("[firebase] firebaseAuthUser updated: $it") }
            .stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = firebaseUser?.firebaseAuthUser
            )

    private fun GoogleAuthData.getAuthCredential(): AuthCredential {
        return GoogleAuthProvider.credential(idToken, accessToken)
    }

    override suspend fun signInAnonymously(): ResultEx<FirebaseAuthUser> {
        log("signInAnonymously()")
        return try {
            val authResult = firebaseAuth.signInAnonymously()
            val authResultUser = authResult.user ?: return ResultEx.Error(Exception("No firebaseAuthUser"))
            ResultEx.Success(authResultUser.firebaseAuthUser)
        } catch (ex: Exception) {
            Log.w(ex, "[firebase] ${ex.message}")
            ResultEx.Error(ex)
        }.also {
            log("signInAnonymously() result=$it")
        }
    }

    override suspend fun signIn(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> {
        log("signIn()")

        if (signInProviderCredentials is SignInProviderCredentials.Email) {
            return signInWithEmail(signInProviderCredentials)
        }

        val credential = when (val credentialResult = authCredential(signInProviderCredentials)) {
            is ResultEx.Success -> credentialResult.data
            is ResultEx.Error -> return credentialResult
        }
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential)
            val authResultUser = authResult.user ?: return ResultEx.Error(Exception("No firebaseAuthUser"))

            // Special handling to save user's display name from Apple
            if (signInProviderCredentials is SignInProviderCredentials.Apple) {
                appleAuthManager.updateDisplayName(authResultUser, signInProviderCredentials.appleAuthSignInResult)
                _firebaseAuthUser.value = authResultUser
            }

            if(signInProviderCredentials is SignInProviderCredentials.Google) {
                updateGoogleSignInDisplayNameAndPhotoUrl(authResultUser, signInProviderCredentials.googleAuthData)
                _firebaseAuthUser.value = authResultUser
            }

            ResultEx.Success(authResultUser.firebaseAuthUser)
        } catch (ex: Exception) {
            Log.w(ex, "[firebase] ${ex.message}")
            ResultEx.Error(ex)
        }
    }

    private suspend fun updateGoogleSignInDisplayNameAndPhotoUrl(firebaseUser: FirebaseUser, googleAuthData: GoogleAuthData): Boolean {
        firebaseUser.updateProfile(displayName = googleAuthData.name)
        firebaseUser.updateProfile(photoUrl = googleAuthData.photoUrl)
        return true
    }

    // Not to be used in production. This is only for testing purposes.
    private suspend fun signInWithEmail(signInProviderCredentials: SignInProviderCredentials.Email): ResultEx<FirebaseAuthUser> {
        if (!buildConfig.debug) {
            throw Exception("signInWithEmail() should only be used in tests")
        }
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(signInProviderCredentials.email, signInProviderCredentials.password)
            val authResultUser = authResult.user ?: return ResultEx.Error(Exception("No firebaseAuthUser"))
            ResultEx.Success(authResultUser.firebaseAuthUser)
        } catch (ex: FirebaseAuthInvalidUserException) {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(signInProviderCredentials.email, signInProviderCredentials.password)
            val authResultUser = authResult.user ?: return ResultEx.Error(Exception("No firebaseAuthUser"))
            ResultEx.Success(authResultUser.firebaseAuthUser)
        } catch (ex: Exception) {
            Log.w(ex, "[firebase] ${ex.message}")
            ResultEx.Error(ex)
        }
    }

    override suspend fun linkWithCredential(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> {
        log("linkWithCredential()")

        // Special handling for linking Apple credentials
        if (signInProviderCredentials is SignInProviderCredentials.Apple) {
            return handleAppleLinkWithCredentials(signInProviderCredentials)
        }

        val credential = when (val credentialResult = authCredential(signInProviderCredentials)) {
            is ResultEx.Success -> credentialResult.data
            is ResultEx.Error -> return credentialResult
        }
        val currentUser = firebaseUser ?: return ResultEx.Error(Exception("No current user"))
        return try {
            val authResult = currentUser.linkWithCredential(credential)
            val authResultUser = authResult.user ?: return ResultEx.Error(Exception("No firebaseAuthUser"))
            if (signInProviderCredentials is SignInProviderCredentials.Google) {
                updateGoogleSignInDisplayNameAndPhotoUrl(authResultUser, signInProviderCredentials.googleAuthData)
            }
            _firebaseAuthUser.value = authResultUser
            ResultEx.Success(authResultUser.firebaseAuthUser)
        } catch (ex: FirebaseAuthUserCollisionException) {
            Log.w(ex, "[firebase] ${ex.message}")
            ResultEx.Error(FirebaseAccountAlreadyLinkedException)
        } catch (ex: Exception) {
            Log.w(ex, "[firebase] ${ex.message}")
            ResultEx.Error(ex)
        }
    }

    private suspend fun handleAppleLinkWithCredentials(signInProviderCredentials: SignInProviderCredentials.Apple): ResultEx<FirebaseAuthUser> {
        when (val linkResult = appleAuthManager.linkWithCredential(signInProviderCredentials.appleAuthSignInResult)) {
            is ResultEx.Error -> {
                return linkResult
            }
            is ResultEx.Success -> {
                val firebaseUser = firebaseUser ?: return ResultEx.Error(Exception("No firebaseAuthUser"))
                if (firebaseUser.uid != linkResult.data) {
                    crashTracking.logNonFatalException(Exception("Firebase user ID mismatch, ${firebaseUser.uid} != ${linkResult.data}"))
                    return ResultEx.Error(Exception("Firebase user ID mismatch"))
                }
                appleAuthManager.updateDisplayName(firebaseUser, signInProviderCredentials.appleAuthSignInResult)
                _firebaseAuthUser.value = firebaseUser
                return ResultEx.Success(firebaseUser.firebaseAuthUser)
            }
        }
    }

    override suspend fun signOut() {
        log("signOut()")
        firebaseAuth.signOut()
    }

    override suspend fun deleteUser(signInProviderCredentials: SignInProviderCredentials): ResultEx<Boolean> {
        log("deleteUser()")
        val credential = when (val credentialResult = authCredential(signInProviderCredentials)) {
            is ResultEx.Success -> credentialResult.data
            is ResultEx.Error -> return credentialResult
        }
        val currentUser = firebaseUser ?: return ResultEx.Error(IllegalStateException("No current Firebase user"))
        return try {
            currentUser.reauthenticate(credential)

            // Revoke token for apple auth
            if (signInProviderCredentials is SignInProviderCredentials.Apple) {
                log("Revoking token for Apple")
                val result = appleAuthManager.revokeToken(signInProviderCredentials.appleAuthSignInResult)
                if (result is ResultEx.Error) {
                    Log.e(result.exception, "Failed to revoke token for Apple")
                    crashTracking.logNonFatalException(result.exception)
                    return result
                }
            }

            currentUser.delete()
            ResultEx.Success(true)
        } catch (ex: Exception) {
            Log.w(ex, ex.message)
            ResultEx.Error(ex)
        }
    }

    override suspend fun getRefreshedAuthTokenForCurrentUser(): String? {
        log("getRefreshedAuthTokenForCurrentUser()")
        try {
            return firebaseUser?.getIdTokenResult(true)?.token
        } catch (ex: Exception) {
            Log.w(ex, ex.message)
            return null
        }
    }

    private fun authCredential(signInProviderCredentials: SignInProviderCredentials): ResultEx<AuthCredential> =
        when (signInProviderCredentials) {
            is SignInProviderCredentials.Apple ->
                appleAuthManager.appleOAuthCredential(signInProviderCredentials.appleAuthSignInResult)

            is SignInProviderCredentials.Google ->
                ResultEx.Success(signInProviderCredentials.googleAuthData.getAuthCredential())

            is SignInProviderCredentials.Email -> {
                if (!buildConfig.debug) {
                    throw Exception("authCredential() should only be used in tests")
                }
                ResultEx.Success(
                    EmailAuthProvider.credential(
                        signInProviderCredentials.email,
                        signInProviderCredentials.password
                    )
                )
            }
        }

    private fun log(message: String, vararg str: Any? = arrayOf<Any?>(null)) {
        Log.d("[firebase] $message", *str)
    }

    init {
        require(process.isDefaultProcess) { "Firebase only supports operation from the default process." }
    }
}