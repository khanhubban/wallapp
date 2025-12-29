package wallapp.auth.firebase

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.result.ResultEx
import wallapp.signin.SignInProviderCredentials

class FirebaseAuthManagerMock(initialFirebaseAuthUser: FirebaseAuthUser? = null) : FirebaseAuthManager {

    var signInAnonymouslyResult: ResultEx<FirebaseAuthUser> = ResultEx.Success(firebaseAuthUserAnonymousMock)
    var signInWithCredentialResult: ResultEx<FirebaseAuthUser> = ResultEx.Success(firebaseAuthUserMock)
    var linkWithCredentialsResult: ResultEx<FirebaseAuthUser> = ResultEx.Success(firebaseAuthUserMock)
    var linkWithCredentialsResultUser = firebaseAuthUserMock
    var deleteUserResult = true

    var userExists = initialFirebaseAuthUser != null

    override val firebaseAuthUser: MutableStateFlow<FirebaseAuthUser?> = MutableStateFlow(initialFirebaseAuthUser)

    override suspend fun signInAnonymously(): ResultEx<FirebaseAuthUser> {
        if (signInAnonymouslyResult is ResultEx.Success) {
            firebaseAuthUser.value = (signInAnonymouslyResult as ResultEx.Success<FirebaseAuthUser>).data
        }
        return signInAnonymouslyResult
    }

    override suspend fun signIn(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> {
        if (signInWithCredentialResult is ResultEx.Success) {
            userExists = true
            firebaseAuthUser.value = (signInWithCredentialResult as ResultEx.Success<FirebaseAuthUser>).data
        }
        return signInWithCredentialResult
    }

    override suspend fun linkWithCredential(signInProviderCredentials: SignInProviderCredentials): ResultEx<FirebaseAuthUser> {
        if (linkWithCredentialsResult is ResultEx.Success) {
            userExists = true
            firebaseAuthUser.value = (linkWithCredentialsResult as ResultEx.Success<FirebaseAuthUser>).data
        }
        return linkWithCredentialsResult
    }

    override suspend fun signOut() {
        firebaseAuthUser.value = null
    }

    override suspend fun deleteUser(signInProviderCredentials: SignInProviderCredentials): ResultEx<Boolean> {
        if (deleteUserResult) {
            userExists = false
            firebaseAuthUser.value = null
        }
        return if (deleteUserResult) {
            ResultEx.Success(deleteUserResult)
        } else {
            ResultEx.Error(Exception("Error deleting user"))
        }
    }

    override suspend fun getRefreshedAuthTokenForCurrentUser(): String? {
        return null
    }

    companion object {
        val firebaseAuthUserMock = FirebaseAuthUser(
            userId = "uid",
            displayName = "displayName",
            email = "email",
            photoUrl = "photoUrl",
            isAnonymous = false,
            providerId = "providerId",
        )
        val firebaseAuthUserAnonymousMock = FirebaseAuthUser(
            userId = "uid",
            displayName = "",
            email = "",
            photoUrl = "",
            isAnonymous = true,
            providerId = "providerId",
        )
    }
}