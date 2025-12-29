package wallapp.pixel.toolbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class ToolbarViewState(
    val navigationIcon: MenuItem? = null,
    val title: MenuItem? = null,
    val centeredTitle: Boolean = true,
    val actionItems: List<MenuItem>? = null,
    val height: Dp = DefaultHeight,
    val containerColorOverride: ColorToken = ColorToken.ThemeSurfaceVariant,
): ViewState {

    companion object {

        val Preset = ToolbarViewState(
            navigationIcon = null,
            title = null,
            actionItems = null,
        )

        val DefaultHeight = 56.dp
    }
}
