package wallapp.pixel.menu

import androidx.compose.ui.unit.Dp
import wallapp.unit.Height
import wallapp.unit.Width


val MenuItem.height: Dp?
    get() = when (this) {
        is MenuItem.MenuItemLabel -> height?.let { if (it is Height.HeightDp) it.dp else null }
        is MenuItem.MenuItemIcon -> height?.dp
        is MenuItem.MenuItemImage -> imageViewState.viewSpec.height
        is MenuItem.MenuItemButton -> height?.dp
        is MenuItem.MenuItemContainer -> height?.dp
        else -> null
    }

val MenuItem.width: Dp?
    get() = when (this) {
        is MenuItem.MenuItemLabel -> width?.let { if (it is Width.WidthDp) it.dp else null }
        is MenuItem.MenuItemIcon -> width?.dp
        is MenuItem.MenuItemImage -> imageViewState.viewSpec.width
        is MenuItem.MenuItemButton -> width?.dp
        is MenuItem.MenuItemContainer -> width?.dp
        else -> null
    }