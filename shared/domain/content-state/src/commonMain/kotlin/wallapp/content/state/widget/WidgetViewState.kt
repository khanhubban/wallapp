package wallapp.content.state.widget

import androidx.compose.runtime.Immutable
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.ViewState

@Immutable
data class WidgetViewState(
    val menuItem: MenuItem,
) : ViewState
