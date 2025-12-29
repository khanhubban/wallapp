package wallapp.content.state.promo

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class PromoViewState(
    val image: Image,
    val viewEventHandler: ViewEventHandler,
) : ViewState
