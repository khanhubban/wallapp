package wallapp.pixel.selection

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.EnumInterop
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewState

@EnumInterop.Enabled
enum class SelectionViewStyle {
    Radio,
    Button,
}

@Immutable
data class SelectionViewState(
    val eventSink: SelectionViewEventSink,
    val shape: ShapeSpec,
    val key: Any,
    val menuItem: MenuItem,
    val selected: Boolean,
    val selectionViewStyle: SelectionViewStyle = SelectionViewStyle.Radio,
    val centerHorizontally: Boolean = false,
) : ViewState
