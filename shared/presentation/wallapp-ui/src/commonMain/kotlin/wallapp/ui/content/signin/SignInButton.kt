package wallapp.ui.content.signin

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.ui.account.SignInWithAppleButton
import wallapp.ui.account.SignInWithGoogleButton

@Composable
fun SignInButton(
    render: Render,
    viewState: SignInButtonViewState,
    modifier: Modifier = Modifier,
    lightTheme: Boolean = false,
    showBorder: Boolean = false,
) {
    when (viewState) {
        is SignInButtonViewState.Apple -> {
            SignInButtonApple(
                render = render,
                viewState = viewState,
                lightTheme = lightTheme,
                showBorder = showBorder,
                modifier = modifier,
            )
        }
        is SignInButtonViewState.Google -> {
            SignInButtonGoogle(
                render = render,
                viewState = viewState,
                lightTheme = lightTheme,
                showBorder = showBorder,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun SignInButtonApple(
    render: Render,
    viewState: SignInButtonViewState.Apple,
    lightTheme: Boolean,
    showBorder: Boolean,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val width = viewSpec.width
    val height = viewSpec.height
    val image = if (lightTheme) { viewState.imageDark } else { viewState.imageLight }
    val label = viewState.label
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!
    val viewEventHandler = viewState.viewEventHandler

    SignInWithAppleButton(
        modifier = modifier
            .width(width)
            .height(height),
        render = render,
        lightTheme = lightTheme,
        image = image,
        label = label,
        viewEventHandler = viewEventHandler,
        showBorder = showBorder,
        shape = shape,
    )
}

@Composable
fun SignInButtonGoogle(
    render: Render,
    viewState: SignInButtonViewState.Google,
    lightTheme: Boolean,
    showBorder: Boolean,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val width = viewSpec.width
    val height = viewSpec.height
    val image = viewState.image
    val label = viewState.label
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!
    val viewEventHandler = viewState.viewEventHandler

    SignInWithGoogleButton(
        modifier = modifier
            .width(width)
            .height(height),
        render = render,
        lightTheme = lightTheme,
        image = image,
        label = label,
        viewEventHandler = viewEventHandler,
        showBorder = showBorder,
        shape = shape,
    )
}