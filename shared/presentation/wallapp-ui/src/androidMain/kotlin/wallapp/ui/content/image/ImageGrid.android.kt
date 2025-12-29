package wallapp.ui.content.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import wallapp.pixel.render.Render

@Preview
@Composable
fun ImageGridPreview() {
    ImageGrid(
        render = Render.Preset,
        items = listOf(
            listOf(
                ImageGridItem.Preset,
                ImageGridItem.Preset,
                ImageGridItem.Preset,
                ImageGridItem.Preset,
            )
        ),
    )
}
