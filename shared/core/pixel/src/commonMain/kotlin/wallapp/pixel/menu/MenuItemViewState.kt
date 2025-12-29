package wallapp.pixel.menu

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewState


@Immutable
data class MenuItemViewState(
    /**
     * menu items will be displayed in a column
     */
    val menuItems: List<MenuItem>,
    val itemPadding: Dp = 0.dp,
    val centerItems: Boolean = false,
) : ViewState {

    constructor(menuItem: MenuItem, centerItems: Boolean = false)
            : this(listOf(menuItem), centerItems = centerItems)
}
