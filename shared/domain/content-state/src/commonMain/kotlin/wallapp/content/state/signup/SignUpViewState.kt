package wallapp.content.state.signup

import androidx.compose.runtime.Immutable
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.content.state.upgrade.plus.button.UpgradeButtonViewState
import wallapp.image.Image
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.text.Text

@Immutable
data class SignUpViewState(
    val logoStatic: Image,
    val welcomeLabel: Text,
    val appleSignInButtonViewState: SignInButtonViewState.Apple?,
    val googleSignButtonInViewState: SignInButtonViewState.Google?,
    val upgradeButton: UpgradeButtonViewState?,
    val skipLabel: Text,
    val skipOnClick: () -> Unit,
) : ScreenViewState
