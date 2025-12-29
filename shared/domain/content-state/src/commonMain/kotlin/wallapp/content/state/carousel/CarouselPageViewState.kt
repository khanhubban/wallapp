package wallapp.content.state.carousel

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class CarouselPageViewState(
    val view: View,
    val onClick: ViewEventHandler?,
) : ViewState
