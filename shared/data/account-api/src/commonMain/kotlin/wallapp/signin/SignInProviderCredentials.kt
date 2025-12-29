package wallapp.signin

import wallapp.auth.apple.AppleAuthSignInResult
import wallapp.auth.google.GoogleAuthData

sealed interface SignInProviderCredentials {

    data class Apple(val appleAuthSignInResult: AppleAuthSignInResult) : SignInProviderCredentials
    data class Google(val googleAuthData: GoogleAuthData) : SignInProviderCredentials
    data class Email(val email: String, val password: String) : SignInProviderCredentials
}