package wallapp.signin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import wallapp.auth.apple.AppleAuthSignInResult
import wallapp.auth.google.GoogleAuthManager
import wallapp.auth.google.GoogleAuthSignInResult
import wallapp.log.Log
import wallapp.result.ResultEx

class SignInProviderControllerDefault(
    private val googleAuthManager: GoogleAuthManager,
    private val coroutineScopeIo: CoroutineScope,
) : SignInProviderController {

    private val _signInNavigationEvents = MutableSharedFlow<SignInNavigationEvent>()
    override val signInNavigationEvents: Flow<SignInNavigationEvent> = _signInNavigationEvents.asSharedFlow()

    private val _signInProviderResult = Channel<ResultEx<SignInProviderCredentials>>()

    override suspend fun performProviderSignIn(signInProvider: SignInProvider): ResultEx<SignInProviderCredentials> {
        Log.d("[firebase] SignInProviderControllerDefault.performProviderSignIn($signInProvider)")
        return _signInProviderResult.receiveAsFlow()
            .onStart {
                when (signInProvider) {
                    SignInProvider.Apple -> {
                        _signInNavigationEvents.emit(SignInNavigationEvent.NavigateToAppleSignIn)
                    }
                    SignInProvider.Google -> {
                        _signInNavigationEvents.emit(SignInNavigationEvent.NavigateToGoogleSignIn)
                    }
                    SignInProvider.Email -> {
                        throw IllegalStateException("Email sign in is not supported")
                    }
                }
            }
            .first()
    }

    override fun onAppleSignInResult(result: AppleAuthSignInResult) {
        coroutineScopeIo.launch {
            _signInProviderResult.send(
                ResultEx.Success(SignInProviderCredentials.Apple(result))
            )
        }
    }

    override fun onGoogleSignInResult(result: GoogleAuthSignInResult) {
        coroutineScopeIo.launch {
            try {
                val googleSignInResult = googleAuthManager.onNewSignIn(result)
                if (googleSignInResult is ResultEx.Error) {
                    Log.w("[firebase] Failed to sign into Google")
                    _signInProviderResult.send(ResultEx.Error(googleSignInResult.exception))
                    return@launch
                }
                val googleAuthData = googleAuthManager.getGoogleAuthData()
                Log.i("[firebase] Google auth tokens: $googleAuthData")
                if (googleAuthData == null) {
                    _signInProviderResult.send(ResultEx.Error(Exception("Failed to get Google auth tokens")))
                    return@launch
                }
                _signInProviderResult.send(
                    ResultEx.Success(SignInProviderCredentials.Google(googleAuthData))
                )
            } catch (ex: Exception) {
                Log.e(ex, ex.message)
                _signInProviderResult.send(ResultEx.Error(ex))
            }
        }
    }

    override suspend fun performProviderSignOut(signInProvider: SignInProvider?) {
        Log.d("[firebase] SignInProviderControllerDefault.performProviderSignOut($signInProvider)")
        if (signInProvider == null) {
            // Sign out of all providers which is only Google for now
            googleAuthManager.signOut()
            return
        }
        when (signInProvider) {
            SignInProvider.Apple -> {
                // Apple doesn't have an explicit sign out mechanism
            }
            SignInProvider.Google -> {
                googleAuthManager.signOut()
            }
            SignInProvider.Email -> {
                throw IllegalStateException("Email sign out is not supported")
            }
        }
    }
}