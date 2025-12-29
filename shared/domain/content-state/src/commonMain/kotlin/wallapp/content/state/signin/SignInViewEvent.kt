package wallapp.content.state.signin

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
interface SignInViewEvent : ViewEvent {

    @Immutable
    data object GoogleSignIn : SignInViewEvent

    @Immutable
    data object AppleSignIn : SignInViewEvent
}

typealias SignInViewEventSink = (SignInViewEvent) -> Unit