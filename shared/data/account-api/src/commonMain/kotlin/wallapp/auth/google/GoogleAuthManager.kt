package wallapp.auth.google

import wallapp.result.ResultEx

interface GoogleAuthManager {
    suspend fun isSignedIn(): Boolean
    fun onNewSignIn(result: GoogleAuthSignInResult): ResultEx<Unit>
    suspend fun signOut()
    suspend fun revokeAppAccess()
    fun getGoogleAuthData(): GoogleAuthData?
    fun clearToken(token: String)
}


const val GOOGLE_ACCOUNT_ERROR = "Unable to sign in to Google. Please use a different account"
const val SCOPE_EMAIL_FOR_OAUTH2 = "oauth2:https://www.googleapis.com/auth/userinfo.email"