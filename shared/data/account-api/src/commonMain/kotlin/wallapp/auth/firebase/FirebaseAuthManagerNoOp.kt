package wallapp.auth.firebase

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.result.ResultEx
import wallapp.signin.SignInProviderCredentials

object FirebaseAuthManagerNoOp : FirebaseAuthManager {

    override val firebaseAuthUser: MutableStateFlow<FirebaseAuthUser?> = MutableStateFlow(null)

    override suspend fun signInAnonymously(): ResultEx<FirebaseAuthUser> = ResultEx.Error(Exception("Not implemented"))

    override suspend fun signIn(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> = ResultEx.Error(Exception("Not implemented"))

    override suspend fun linkWithCredential(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> = ResultEx.Error(Exception("Not implemented"))

    override suspend fun signOut() { }

    override suspend fun deleteUser(signInProviderCredentials: SignInProviderCredentials): ResultEx<Boolean> = ResultEx.Error(Exception("Not implemented"))

    override suspend fun getRefreshedAuthTokenForCurrentUser(): String? = null
}