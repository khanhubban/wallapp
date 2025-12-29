package wallapp.pixel.globaloverlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import wallapp.image.Image
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper

@Composable
actual fun GlobalOverlayContent(
    render: Render,
    viewState: GlobalOverlayViewState.Data,
    modifier: Modifier,
) {
    val image = viewState.image
    val imageSize = viewState.imageSize?.dp
    val message = viewState.message
    val scrimColor = ThemeColorTypeMapper.map(viewState.scrimColor)
    val paddingDefault = render.defaultViewSpec.paddingDefault

    Column(
        modifier = modifier.fillMaxSize().background(color = scrimColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        image?.also {
            Image(
                render,
                image,
                modifier = Modifier.conditional(
                    condition = imageSize != null,
                    ifTrue = { size(imageSize!!) },
                    ifFalse = { fillMaxSize() }
                ),
            )
        }

        if (message != null) {
            Spacer(modifier = Modifier.height(paddingDefault))
            Text(
                text = message,
                modifier = Modifier,
                colorOverride = Color.White,
            )
        }
    }
}