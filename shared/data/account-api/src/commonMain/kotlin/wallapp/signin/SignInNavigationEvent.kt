package wallapp.signin

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class SignInNavigationEvent {
    NavigateToAppleSignIn,
    NavigateToGoogleSignIn,
}