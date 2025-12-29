package wallapp.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
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
    LogoAndIconButton(render, image, label, viewEventHandler, lightTheme = lightTheme, shape, modifier)
//    SignInWithGoogleButtonInternal(modifier, render, lightTheme, image, label, viewEventHandler)
}