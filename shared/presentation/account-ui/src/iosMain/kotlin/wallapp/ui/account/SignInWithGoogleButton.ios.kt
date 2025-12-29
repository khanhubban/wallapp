package wallapp.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kotlinx.cinterop.ExperimentalForeignApi
import wallapp.image.Image
import wallapp.pixel.button.LogoAndIconButton
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler

@OptIn(ExperimentalForeignApi::class)
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
//    SignInWithGoogleButtonUiKit(modifier, kGIDSignInButtonStyleWide, kGIDSignInButtonColorSchemeDark)
//    SignInWithGoogleButtonInternal(modifier, render, lightTheme, image, label, viewEventHandler)
}

//@OptIn(ExperimentalForeignApi::class)
//@Composable
//private fun SignInWithGoogleButtonUiKit(
//    modifier: Modifier,
//    kGIDSignInButtonStyleWide: GIDSignInButtonStyle,
//    kGIDSignInButtonColorSchemeDark: GIDSignInButtonColorScheme,
//) {
//    //    val instance = GIDSignIn.sharedInstance
////    Log.d("Google user: ${instance.currentUser?.toString()}")
//
//    val backgroundColor = Color.Transparent
////    val backgroundColor = MaterialTheme.colorScheme.scrim
//
//    UIKitView(
//        modifier = modifier
//            .background(backgroundColor),
//        factory = {
//            GIDSignInButton().apply {
//                this.style = kGIDSignInButtonStyleWide
//                this.colorScheme = kGIDSignInButtonColorSchemeDark
//            }
//        },
//        background = backgroundColor,
//    )
//}