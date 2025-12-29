package wallapp.account.state.signin

import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.account.SignInMethod
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.content.state.signin.SignInViewEvent
import wallapp.content.state.signin.SignInViewEventSink
import wallapp.network.NetworkState
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.resources.string.StringRepository
import wallapp.result.ResultEx
import wallapp.system.platform.PlatformFeature
import wallapp.view.ViewStateFactory
import wallapp.viewmodel.ViewModel

@Suppress("UNCHECKED_CAST")
class SignInViewModel(
    private val appStateManager: AppStateManager,
    private val viewStateFactory: ViewStateFactory,
    private val accountManager: AccountManager,
    private val globalOverlayManager: GlobalOverlayManager,
    private val strings: StringRepository,
    private val networkState: NetworkState,
) : ViewModel() {

    private val viewEventSink: SignInViewEventSink = { event: SignInViewEvent ->
        when (event) {
            is SignInViewEvent.GoogleSignIn -> googleSignButtonInOnClick()
            is SignInViewEvent.AppleSignIn -> appleSignButtonInOnClick()
        }
    }

    private val googleSignInEventHandler = ViewEventHandler.Event(
        eventSink = viewEventSink as ViewEventSink,
        event = SignInViewEvent.GoogleSignIn,
    )

    private val appleSignInEventHandler = ViewEventHandler.Event(
        eventSink = viewEventSink as ViewEventSink,
        event = SignInViewEvent.AppleSignIn,
    )

    private fun signIn(signInMethod: SignInMethod) {
        if (!networkState.isConnected) {
            appStateManager.navigateToError(ErrorScreen.Network())
        } else {
            viewModelScope.launch {
                toggleGlobalOverlay(true)
                when (val signInResult = accountManager.signIn(signInMethod)) {
                    is ResultEx.Success -> {}
                    is ResultEx.Error -> appStateManager.navigateToError(
                        ErrorScreen.SignIn(signInResult.exception.message ?: "")
                    )
                }
                toggleGlobalOverlay(false)
            }
        }
    }

    private fun appleSignButtonInOnClick() {
        signIn(SignInMethod.Apple)
    }

    private fun googleSignButtonInOnClick() {
        signIn(SignInMethod.Google)
    }

    private val shortLabel: Boolean
        get() = false

    val googleSignInButtonViewState: SignInButtonViewState.Google =
        viewStateFactory.createSignInButtonGoogleViewState(
            googleSignInEventHandler,
            shortLabel = shortLabel,
        )

    val appleSignInButtonViewState: SignInButtonViewState.Apple? =
        if (PlatformFeature.SignInWithAppleSupported) {
            viewStateFactory.createSignInButtonAppleViewState(
                appleSignInEventHandler,
                shortLabel = shortLabel,
            )
        } else {
            null
        }

    private fun toggleGlobalOverlay(show: Boolean) {
        if (show) {
            globalOverlayManager.show(viewStateFactory.createGlobalOverlay(strings.signingYouIn))
        } else {
            globalOverlayManager.hide()
        }
    }
}