package wallapp.ui.content.exhibit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wallapp.content.state.exhibit.ExhibitViewState
import wallapp.image.Image
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text

@Composable
fun Exhibit(
    render: Render,
    viewState: ExhibitViewState,
    modifier: Modifier = Modifier,
) {
    val imageViewState = viewState.imageViewState
    val label = viewState.label
    val scrimColor = MaterialTheme.colorScheme.scrim
    val contentColor = Color.White

    val shapeSpec = viewState.shapeSpec
    val shape = render.shapeMapperComposable.map(shapeSpec)!!
    val borderColor = MaterialTheme.colorScheme.outline
    val borderSize = 1.dp

    Box(
        modifier = modifier
            .clip(shape)
            .border(width = borderSize, color = borderColor, shape = shape),
    ) {
        Image(
            render = render,
            viewState = imageViewState,
            modifier = Modifier.fillMaxSize(),
        )

        if (label != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor),
            )

            Text(
                text = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                colorOverride = contentColor,
            )
        }
    }
}