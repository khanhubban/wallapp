package wallapp.auth.google

import wallapp.log.Log
import wallapp.result.ResultEx

class GoogleAuthManagerIos(
    private val googleAuthCoordinatorForIos: GoogleAuthCoordinatorForIos,
) : GoogleAuthManager {

    init {
        Log.d("GoogleAuthManagerIos.init()")
    }

    override suspend fun isSignedIn(): Boolean {
        Log.d("GoogleAuthManagerIos.isSignedIn()")
        return googleAuthCoordinatorForIos.isSignedIn()
    }

    override fun onNewSignIn(result: GoogleAuthSignInResult): ResultEx<Unit> {
        Log.d("GoogleAuthManagerIos.onNewSignIn(): $result")

        val onNewSignInResult = googleAuthCoordinatorForIos.onNewSignIn(result)
        return if (onNewSignInResult) {
            Log.d("GoogleAuthManagerIos.onNewSignIn(): User exists")
            ResultEx.Success(Unit)
        } else {
            Log.e("GoogleAuthManagerIos.onNewSignIn(): No current user")
            ResultEx.Error(Exception("Google sign in failed"))
        }
    }

    override suspend fun signOut() {
        Log.d("GoogleAuthManagerIos.signOut()")
        googleAuthCoordinatorForIos.signOut()
    }

    override suspend fun revokeAppAccess() {
        Log.d("GoogleAuthManagerIos.revokeAppAccess()")
//        gidSignIn.disconnect()
    }

    override fun getGoogleAuthData(): GoogleAuthData? {
        return googleAuthCoordinatorForIos.getGoogleAuthData().also {
            Log.d("GoogleAuthManagerIos.getGoogleAuthTokens() -> $it")
        }
    }

    override fun clearToken(token: String) {
        Log.d("GoogleAuthManagerIos.clearToken(token: $token)")
        // Clear token logic; may vary depending on your implementation
    }
}
