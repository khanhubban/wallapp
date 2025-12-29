package wallapp.ui.content.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.content.state.signup.SignUpViewState
import wallapp.content.state.upgrade.plus.button.UpgradeButtonViewState
import wallapp.image.Image
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.theme.AppTheme
import wallapp.system.platform.PlatformFeature
import wallapp.ui.content.signin.SignInButton
import wallapp.ui.content.upgrade.plus.button.UpgradeButton

@Composable
fun SignUp(
    render: Render,
    viewState: SignUpViewState,
    modifier: Modifier = Modifier,
) {

    AppTheme(
        render = render,
    ) {
        SignUpContent(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    }
}

@Composable
fun SignUpContent(
    render: Render,
    viewState: SignUpViewState,
    modifier: Modifier = Modifier,
) {
    val welcomeLabel = viewState.welcomeLabel
    val skipLabel = viewState.skipLabel
    val skipOnClick = viewState.skipOnClick

    val buttonWidth = 300.dp
    val buttonHeight = 48.dp
    val appleSignInButton = viewState.appleSignInButtonViewState
    val googleSignInButton = viewState.googleSignButtonInViewState

    val upgradeButton = viewState.upgradeButton

    Box(
        modifier = modifier.fillMaxHeight(),
    ) {
        SignUpContent(
            render,
            welcomeLabel,
            buttonWidth,
            buttonHeight,
            appleSignInButton,
            googleSignInButton,
            upgradeButton,
            skipOnClick,
            skipLabel,
        )
    }
}

@Composable
private fun BoxScope.SignUpContent(
    render: Render,
    welcomeLabel: Text,
    buttonWidth: Dp,
    buttonHeight: Dp,
    appleSignInButtonViewState: SignInButtonViewState.Apple?,
    googleSignInButtonViewState: SignInButtonViewState.Google?,
    upgradeButton: UpgradeButtonViewState?,
    skipOnClick: () -> Unit,
    skipLabel: Text,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val upgradeButtonHeight = buttonHeight * 1.2f

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = render.windowFrame.navigationBarHeight + paddingDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        appleSignInButtonViewState?.let {
            SignInButton(
                render = render,
                viewState = appleSignInButtonViewState,
                lightTheme = true,
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
            )

            Spacer(modifier = Modifier.height(paddingDefault))
        }

        googleSignInButtonViewState?.let {
            SignInButton(
                render = render,
                viewState = googleSignInButtonViewState,
                lightTheme = true,
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
            )
        }

        Spacer(modifier = Modifier.height(paddingDefault))

        if (upgradeButton != null) {
            UpgradeButton(
                render = render,
                viewState = upgradeButton,
                modifier = Modifier
                    .width(buttonWidth)
                    .height(upgradeButtonHeight),
            )

            Spacer(modifier = Modifier.height(paddingDefault))
        }

        TextButton(
            onClick = skipOnClick,
            colors = ButtonDefaults.textButtonColors(contentColor = contentColor),
        ) {
            Text(skipLabel, colorOverride = contentColor)
        }
    }
}

@Composable
fun BoxScope.SignUpLogo(
    render: Render,
    logo: Image?,
    showAnimatedLogo: Boolean,
    welcomeLabel: Text,
    contentColor: Color,
) {
    val paddingLarge = render.defaultViewSpec.paddingLarge
    val paddingDefault = render.defaultViewSpec.paddingDefault

    if (logo == null) return
    if (!PlatformFeature.ShowFirstRunLogoSplash) return

    if (showAnimatedLogo) {
        Image(render, logo, modifier = Modifier.fillMaxWidth().fillMaxHeight())
    } else {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = render.windowFrame.statusBarHeight + paddingDefault),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(render, logo, modifier = Modifier.size(160.dp))

            Spacer(modifier = Modifier.height(paddingLarge))

            Text(
                welcomeLabel,
                colorOverride = contentColor,
            )
        }
    }
}