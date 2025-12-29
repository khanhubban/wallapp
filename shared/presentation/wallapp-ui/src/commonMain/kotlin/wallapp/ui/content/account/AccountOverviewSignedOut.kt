package wallapp.ui.content.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text
import wallapp.ui.content.profile.ProfileImage
import wallapp.ui.content.signin.SignInButton

@Composable
fun AccountOverviewSignedOut(
    render: Render,
    viewState: AccountOverviewViewState.SignedOut,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val padding = viewSpec.padding
    val spacerHeight = viewSpec.paddingDefault
    val shape = render.shapeMapperComposable.map(viewSpec.backgroundShapeSpec)!!

    val profileImage = viewState.profileImage
    val profileEventHandler = viewState.profileEventHandler
    val signInMessage = viewState.signInMessage
    val signInButtons = viewState.signInButtons
    val signInButtonsUseLightTheme = viewState.signInButtonsUseLightTheme
    val signInButtonsShowBorder = true

    Column(
        modifier = modifier
            .clip(shape)
            .background(color = MaterialTheme.colorScheme.surface)
            .paddingAx(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (profileImage != null) {
            ProfileImage(render, profileImage, eventHandler = profileEventHandler)
            Spacer(modifier = Modifier.height(spacerHeight))
        }

        if (signInMessage != null) {
            Text(signInMessage)
            Spacer(modifier = Modifier.height(spacerHeight))
        }

        signInButtons.forEachIndexed { index, signInButtonViewState ->
            SignInButton(
                render,
                signInButtonViewState,
                lightTheme = signInButtonsUseLightTheme,
                showBorder = signInButtonsShowBorder,
            )
            if (index < signInButtons.size - 1) {
                Spacer(modifier = Modifier.height(spacerHeight))
            }
        }
    }
}