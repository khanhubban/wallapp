package wallapp.content.state.widget

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewState

@Immutable
data class HorizontalScrollRowViewState(
    val views: List<View>,
) : ViewState
