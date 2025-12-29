package wallapp.pixel.selection

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewState

@Immutable
data class SelectionGroupViewState(
    val eventSink: SelectionViewEventSink,
    val viewSpec: SelectionGroupViewSpec,
    private val items: List<SelectionViewState>,
    val allowMultipleSelections: Boolean,
) : ViewState {

    val selections: List<SelectionViewState> = if (!allowMultipleSelections) {
        // Technically it's possible to have multiple selections in a single selection group,
        // such as when one item was set to selected but the other items have yet to update due to
        // concurrency issues. Ignore this check for now.
//        require(items.filter { it.selected }.size == 1) {
//            "Single selections must have exactly one selection"
//        }

        items.map { selection ->
            // Note: watch this, as this causes selection to fail an equality check, which will
            // cause recompositions.
            val eventSink: SelectionViewEventSink = { event ->
                selection.eventSink(event)
            }
            selection.copy(eventSink = eventSink)
        }
    } else {
        items
    }

}
