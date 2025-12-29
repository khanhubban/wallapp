package wallapp.pixel.navigationbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class NavigationBarViewSpec(
    val navBarItemsHeight: Dp,
    val bottomNavBarItemsYOffset: Dp,
    val edgePadding: Dp = 0.dp,
    val maxOffset: Dp = 0.dp,
) : ViewSpec