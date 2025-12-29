package wallapp.auth.google

import wallapp.result.ResultEx


object GoogleAuthManagerNoOp : GoogleAuthManager {
    override suspend fun isSignedIn(): Boolean { return true }

    override fun onNewSignIn(result: GoogleAuthSignInResult): ResultEx<Unit> = ResultEx.Success(Unit)

    override suspend fun revokeAppAccess() { }

    override suspend fun signOut() { }

    // For testing - Get an access token from https://developers.google.com/oauthplayground
    override fun getGoogleAuthData(): GoogleAuthData? = null

//    override fun getSignedInAccountEmail(): String? = null

    override fun clearToken(token: String) { }
}