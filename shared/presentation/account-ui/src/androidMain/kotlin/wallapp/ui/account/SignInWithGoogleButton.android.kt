package wallapp.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.common.SignInButton
import wallapp.image.Image
import wallapp.pixel.button.LogoAndIconButton
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler

@Composable
actual fun SignInWithGoogleButtonNative(
    modifier: Modifier,
    render: Render,
    lightTheme: Boolean,
    image: Image,
    label: Text,
    shape: Shape,
    viewEventHandler: ViewEventHandler,
) {
    LogoAndIconButton(render, image, label, viewEventHandler, lightTheme = lightTheme, shape, modifier = modifier)
//    SignInWithGoogleButtonInternal(modifier, render, lightTheme, image, label, viewEventHandler)
//    SignInWithGoogleButtonGms(modifier, lightTheme, onClick)
}

@Composable
private fun SignInWithGoogleButtonGms(
    modifier: Modifier,
    lightTheme: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = if (lightTheme) SignInButton.COLOR_LIGHT else SignInButton.COLOR_DARK
    AndroidView(
        modifier = modifier,
//            .fillMaxWidth()
//            .height(height),
        factory = { context ->
            SignInButton(context).apply {
                setStyle(SignInButton.SIZE_WIDE, colorScheme)
                setOnClickListener {
                    onClick.invoke()
                }
            }
        },
        update = { },
    )
}