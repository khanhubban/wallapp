package wallapp.account.signin

interface SignInResult {

    data class Success(
        val email: String,
        val accountSignInResult: AccountSignInResult,
    ) : SignInResult

    data class Error(
        val errorReason: SignInErrorReason,
    ) : SignInResult

}