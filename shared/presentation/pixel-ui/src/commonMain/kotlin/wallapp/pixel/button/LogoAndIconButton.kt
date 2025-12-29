package wallapp.pixel.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler

@Composable
fun LogoAndIconButton(
    render: Render,
    image: Image,
    label: Text,
    viewEventHandler: ViewEventHandler,
    lightTheme: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    logoSize: Dp = 44.dp,
    showBorder: Boolean = false,
) {
    val imageSize = logoSize / 2
    val (backgroundColor, foregroundColor) = if (lightTheme) {
        Color.White to Color.Black
    } else {
        Color.Black to Color.White
    }

    LogoAndIconButton(
        render = render,
        image = {
            Image(
                render,
                image,
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.Center),
            )
        },
        label = {
            Text(
                label,
                modifier = Modifier.align(Alignment.Center),
                colorOverride = foregroundColor,
            )
        },
        viewEventHandler = viewEventHandler,
        backgroundColor = backgroundColor,
        foregroundColor = foregroundColor,
        shape = shape,
        showBorder = showBorder,
        logoSize = logoSize,
        modifier = modifier,
    )
}

@Composable
fun LogoAndIconButton(
    render: Render,
    image: @Composable BoxScope.() -> Unit,
    label: @Composable BoxScope.() -> Unit,
    viewEventHandler: ViewEventHandler,
    backgroundColor: Color,
    foregroundColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    logoSize: Dp = 44.dp,
) {
    val defaultPadding = 12.dp
    val horizontalEdgeWeight = .075f
    val borderSize = 2.dp

    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .fillMaxWidth()
            .conditional(showBorder) { border(borderSize, foregroundColor, shape = shape) }
            .clickable(render) { viewEventHandler.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(horizontalEdgeWeight))

        Box(
            modifier = Modifier
                .size(logoSize)
        ) {
            image()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = defaultPadding)
        ) {
            label()
        }

        Spacer(modifier = Modifier.weight(horizontalEdgeWeight))
    }
}
