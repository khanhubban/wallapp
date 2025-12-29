package wallapp.auth.google

interface GoogleAuthCoordinatorForIos {

    fun isSignedIn(): Boolean
    fun onNewSignIn(result: GoogleAuthSignInResult): Boolean
    fun signOut()
    fun getGoogleAuthData(): GoogleAuthData?
}