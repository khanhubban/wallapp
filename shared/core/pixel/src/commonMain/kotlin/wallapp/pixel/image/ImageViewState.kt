package wallapp.pixel.image

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.image.Image
import wallapp.image.ImageSize
import wallapp.image.ImageVideoState
import wallapp.pixel.view.ViewState

@Immutable
data class ImageViewState(
    val image: Image,
    val viewSpec: ImageViewSpec,
    val imageSize: ImageSize?,
    // Special case for specifying video properties. #1958, #2016.
    val videoState: ImageVideoState? = null,
) : ViewState {

    companion object {
        val Preset = ImageViewState(
            image = Image.Preset,
            viewSpec = ImageViewSpec.Preset,
            imageSize = null,
            videoState = null,
        )
    }
}

fun Image.toImageViewState(width: Dp, height: Dp): ImageViewState {
    return ImageViewState(
        image = this,
        viewSpec = ImageViewSpec(width = width, height = height, shapeSpec = null),
        imageSize = null,
        videoState = null,
    )
}