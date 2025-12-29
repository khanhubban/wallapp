package wallapp.content.state.explore

import wallapp.content.state.carousel.CarouselViewSpec
import wallapp.content.state.carousel.CarouselViewState
import wallapp.theme.ColorToken

data class HighlightCarouselViewState(
    val carouselViewSpec: CarouselViewSpec,
    val carouselViewState: CarouselViewState,
    val backgroundColor: ColorToken,
    val autoScrollFirstDelayInSeconds: Int,
    val autoScrollDelayInSeconds: Int,
    val onPageChange: (Int) -> Unit,
)