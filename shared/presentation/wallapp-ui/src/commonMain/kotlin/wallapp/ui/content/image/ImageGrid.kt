package wallapp.ui.content.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.image.Image
import wallapp.pixel.render.Render


//@Composable
//fun ImageGrid(
//    render: Render,
//    items: List<ImageGridItem>,
//    modifier: Modifier = Modifier,
//) {
//    ImageGrid(render, listOf(items), modifier)
//}

@Composable
fun ImageGrid(
    render: Render,
    items: List<List<ImageGridItem>>,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    Column(modifier = modifier) {
        items.forEach { images ->
            Row(
                modifier = Modifier.weight(1f),
            ) {
                ImageGridRow(render, images, modifier, alignment)
            }
        }
    }
}

@Composable
fun ImageGridRow(
    render: Render,
    images: List<ImageGridItem>,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    Row(modifier) {
        images.forEach { image ->
            ImageContent(
                render,
                image,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                alignment = alignment,
            )
        }
    }
}

@Composable
fun ImageContent(
    render: Render,
    imageGridItem: ImageGridItem,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    Box(
        modifier = modifier,
    ) {
        Image(
            render = render,
            viewState = imageGridItem.imageViewState,
            modifier = Modifier.fillMaxSize(),
            alignment = alignment,
        )
    }
}