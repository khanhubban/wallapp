package wallapp.account.signin


interface SignInManagerListeners {

    fun onSignInStart() {}
    fun onSignInComplete(signInResult: SignInResult) {}

    fun transitionToGoogleSignIn() {}

    companion object {
        val NoOp = object : SignInManagerListeners {}
    }
}


fun SignInManagerListeners.onSignInCompleteError(errorReason: SignInErrorReason) {
    onSignInComplete(SignInResult.Error(errorReason))
}