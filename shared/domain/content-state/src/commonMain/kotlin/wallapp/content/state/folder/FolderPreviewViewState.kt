package wallapp.content.state.folder

import androidx.compose.ui.unit.Dp
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState

data class FolderPreviewViewState(
    val title: Text,
    val folderPreviewShapeSpec: ShapeSpec,
    val wallpapers: List<ImageViewState>,
    val titlePadding: Dp,
    val bottomShadowImage: ImageViewState,
) : ViewState