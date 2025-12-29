package wallapp.signin

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.Flow
import wallapp.auth.apple.AppleAuthSignInResult
import wallapp.auth.google.GoogleAuthSignInResult
import wallapp.result.ResultEx

interface SignInProviderController {

    @FlowInterop.Enabled
    val signInNavigationEvents: Flow<SignInNavigationEvent>

    suspend fun performProviderSignIn(signInProvider: SignInProvider): ResultEx<SignInProviderCredentials>

    fun onGoogleSignInResult(result: GoogleAuthSignInResult)

    fun onAppleSignInResult(result: AppleAuthSignInResult)

    suspend fun performProviderSignOut(signInProvider: SignInProvider?)
}

