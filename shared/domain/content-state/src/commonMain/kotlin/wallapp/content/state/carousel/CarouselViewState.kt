package wallapp.content.state.carousel

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewState

@Immutable
data class CarouselViewState(
    val pages: List<CarouselPageViewState>,
    val initialPage: Int,
//    private val currentPage: Int,
//    private val onPageChange: (Int) -> Unit,
): ViewState
