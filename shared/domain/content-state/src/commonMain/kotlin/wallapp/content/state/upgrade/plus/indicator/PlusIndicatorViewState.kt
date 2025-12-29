package wallapp.content.state.upgrade.plus.indicator

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class PlusIndicatorViewState(
    val image: Image,
    val width: Dp = DefaultSize,
    val height: Dp = DefaultSize,
    val viewEventHandler: ViewEventHandler?,
) : ViewState {

    companion object {
        val DefaultSize = 36.dp
    }

}
