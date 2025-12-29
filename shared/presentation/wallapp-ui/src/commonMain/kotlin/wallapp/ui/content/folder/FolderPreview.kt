package wallapp.ui.content.folder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import wallapp.content.state.folder.FolderPreviewViewState
import wallapp.image.Image
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text

@Composable
fun FolderPreview(
    render: Render,
    viewState: FolderPreviewViewState,
    modifier: Modifier = Modifier,
) {

    Box {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.align(Alignment.Center)
                .clip(shape = render.shapeMapperComposable.map(viewState.folderPreviewShapeSpec)!!),
        ) {
            viewState.wallpapers.forEach { wallpaper ->
                Image(render = render, viewState = wallpaper)
            }
        }
        Image(
            render = render,
            viewState = viewState.bottomShadowImage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .rotate(180f)
                .padding(),
        )
        Text(
            viewState.title,
            modifier = Modifier.padding(viewState.titlePadding)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
        )
    }
}
