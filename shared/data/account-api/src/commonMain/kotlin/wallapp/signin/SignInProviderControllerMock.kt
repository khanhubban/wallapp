package wallapp.signin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import wallapp.auth.apple.AppleAuthSignInResult
import wallapp.auth.google.GoogleAuthData
import wallapp.auth.google.GoogleAuthSignInResult
import wallapp.result.ResultEx

class SignInProviderControllerMock : SignInProviderController {

    var signInProvider: SignInProvider? = null

    var performProviderSignInResult: ResultEx<SignInProviderCredentials> =
        ResultEx.Success(SignInProviderCredentials.Google(GoogleAuthData.Preset))

    override val signInNavigationEvents: Flow<SignInNavigationEvent> = emptyFlow()

    override suspend fun performProviderSignIn(signInProvider: SignInProvider): ResultEx<SignInProviderCredentials> {
        return performProviderSignInResult
    }

    override fun onAppleSignInResult(result: AppleAuthSignInResult) {
    }

    override fun onGoogleSignInResult(result: GoogleAuthSignInResult) {
    }

    override suspend fun performProviderSignOut(signInProvider: SignInProvider?) {
        if (this.signInProvider == signInProvider) {
            this.signInProvider = null
        }
    }
}