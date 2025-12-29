package wallapp.pixel.tab

import androidx.compose.runtime.Immutable
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEvent
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class TabViewState(
    val headerSelected: MenuItem,
    val headerUnselected: MenuItem,
    val view: View?,
    val selectedContentColorToken: ColorToken? = null,
    val viewEvent: ViewEvent? = null,
) : ViewState
