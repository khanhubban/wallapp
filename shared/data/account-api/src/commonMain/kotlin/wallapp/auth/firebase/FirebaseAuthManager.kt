package wallapp.auth.firebase

import kotlinx.coroutines.flow.StateFlow
import wallapp.result.ResultEx
import wallapp.signin.SignInProviderCredentials

interface FirebaseAuthManager {

    val firebaseAuthUser: StateFlow<FirebaseAuthUser?>

    suspend fun signInAnonymously(): ResultEx<FirebaseAuthUser>

    suspend fun signIn(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser>

    suspend fun linkWithCredential(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser>

    suspend fun signOut()

    suspend fun deleteUser(signInProviderCredentials: SignInProviderCredentials): ResultEx<Boolean>

    suspend fun getRefreshedAuthTokenForCurrentUser(): String?
}


val FirebaseAuthManager.userSignedIn: Boolean
    get() = firebaseAuthUser.value != null
