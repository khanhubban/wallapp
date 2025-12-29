package wallapp.ui.content.image

import wallapp.pixel.image.ImageViewState

// Can be moved out of :ui module when [ContentScale] is wrapped.
data class ImageGridItem(
    val imageViewState: ImageViewState,
) {

    companion object {
        val Preset = ImageGridItem(ImageViewState.Preset)
    }
}
