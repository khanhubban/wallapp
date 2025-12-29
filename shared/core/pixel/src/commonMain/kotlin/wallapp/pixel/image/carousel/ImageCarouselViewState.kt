package wallapp.pixel.image.carousel

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.image.carousel.ImageCarouselType
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.view.ViewState

@Immutable
data class ImageCarouselViewState(
    val type: ImageCarouselType,
    val scrim: Image?,
    val imageViewStates: List<ImageViewState>,
    val initialPage: Int = 0,
    val autoChangeDuration: Long = 3500,
    val userScrollEnabled: Boolean = false,
    val onPageChanged: (index: Int) -> Unit = {},
) : ViewState
