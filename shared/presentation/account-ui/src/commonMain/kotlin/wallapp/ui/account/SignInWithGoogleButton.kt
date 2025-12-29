package wallapp.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.button.LogoAndIconButton
import wallapp.pixel.clickable.clickable
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler

@Composable
fun SignInWithGoogleButton(
    modifier: Modifier,
    render: Render,
    lightTheme: Boolean,
    image: Image,
    label: Text,
    shape: Shape,
    viewEventHandler: ViewEventHandler,
    showBorder: Boolean,
) {
    LogoAndIconButton(
        render,
        image,
        label,
        viewEventHandler,
        lightTheme,
        shape,
        modifier = modifier,
        showBorder = showBorder,
    )
}

@Composable
expect fun SignInWithGoogleButtonNative(
    modifier: Modifier,
    render: Render,
    lightTheme: Boolean,
    image: Image,
    label: Text,
    shape: Shape,
    viewEventHandler: ViewEventHandler,
)

@Composable
internal fun SignInWithGoogleButtonInternal(
    modifier: Modifier,
    render: Render,
    lightTheme: Boolean,
    image: Image,
    label: Text,
    viewEventHandler: ViewEventHandler,
) {
    val logoSize = 52.dp
    val imageSize = logoSize / 2
    val borderSize = 1.dp
    val foregroundColor = Color.White
    val backgroundColor = Color(0xff4285f4)

    Row(
        modifier = modifier
            .border(borderSize, backgroundColor, RectangleShape)
            .clickable(render) { viewEventHandler.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(logoSize)
                .background(foregroundColor),
        ) {
            Image(
                render, image,
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.Center),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(backgroundColor),
        ) {
            Text(
                label,
                modifier = Modifier.align(Alignment.Center),
                colorOverride = foregroundColor,
            )
        }
    }
}
