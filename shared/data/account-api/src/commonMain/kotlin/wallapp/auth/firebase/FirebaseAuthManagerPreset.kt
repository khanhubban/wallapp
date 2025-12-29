package wallapp.auth.firebase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.result.ResultEx
import wallapp.signin.SignInProviderCredentials

class FirebaseAuthManagerPreset : FirebaseAuthManager {

    private val _firebaseAuthUser = MutableStateFlow<FirebaseAuthUser?>(null)
    override val firebaseAuthUser: StateFlow<FirebaseAuthUser?>
        get() = _firebaseAuthUser

    override suspend fun signInAnonymously(): ResultEx<FirebaseAuthUser> {
        return ResultEx.Success(FirebaseAuthUser.Preset)
    }

    override suspend fun signIn(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> {
        TODO("Not yet implemented")
    }

    override suspend fun linkWithCredential(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        _firebaseAuthUser.value = null
    }

    override suspend fun deleteUser(signInProviderCredentials: SignInProviderCredentials): ResultEx<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getRefreshedAuthTokenForCurrentUser(): String? {
        TODO("Not yet implemented")
    }
}