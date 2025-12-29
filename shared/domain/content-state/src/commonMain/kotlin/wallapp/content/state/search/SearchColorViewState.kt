package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import wallapp.content.state.search.SearchViewEvent.SearchColorToggle
import wallapp.graphics.Color
import wallapp.pixel.view.ViewState

@Immutable
data class SearchColorViewState(
    val eventSink: SearchViewEventSink,
    val event: SearchColorToggle,
    val color: Color,
    val isSelected: Boolean,
    val canShowBorder: Boolean,
) : ViewState
