package wallapp.pixel.selection

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
data class SelectionViewEvent(
    val key: Any,
    val selected: Boolean,
) : ViewEvent

typealias SelectionViewEventSink = (SelectionViewEvent) -> Unit