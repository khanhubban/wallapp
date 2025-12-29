package wallapp.pixel.menu

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.font.TextStyle
import wallapp.image.Image
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken
import wallapp.unit.Alignment
import wallapp.unit.Height
import wallapp.unit.Padding
import wallapp.unit.Width

@Immutable
@SealedInterop.Enabled
sealed class MenuItem {

    @Immutable
    abstract class MenuItemGroup : MenuItem() {
        abstract val menuItems: List<MenuItem>
    }

    @Immutable
    data class MenuItemLabel(
        val text: Text,
        val contentAlignment: Alignment = Alignment.CenterStart,
        val width: Width? = null,
        val height: Height? = null,
        val minTextStyle: TextStyle? = null,
        val autoResize: Boolean = false,
        val onClick: ViewEventHandler?,
    ) : MenuItem()

    @Immutable
    data class MenuItemIcon(
        val icon: Image,
        val tintColor: ColorToken? = DefaultTintColor,
        val width: DpOptional? = null,
        val height: DpOptional? = null,
        val onClick: ViewEventHandler? = null,
    ) : MenuItem() {
        companion object {
            val DefaultTintColor = ColorToken.ThemeOnBackground
        }
    }

    @Immutable
    data class MenuItemImage(
        val imageViewState: ImageViewState,
        val onClick: ViewEventHandler? = null,
    ) : MenuItem()

    @Immutable
    data class MenuItemButton(
        val button: ButtonViewState,
        val width: DpOptional? = null,
        val height: DpOptional? = null,
    ) : MenuItem()

    @Immutable
    data class MenuItemPopup(
        val menuItem: MenuItem,
        val showPopup: Boolean,
        val menuItemGroup: MenuItemGroup,
    ) : MenuItem()

    @Immutable
    data class MenuItemGroupHorizontal(
        override val menuItems: List<MenuItem>,
        val height: Dp,
        val width: DpOptional? = null,
    ) : MenuItemGroup()

    @Immutable
    data class MenuItemGroupVertical(
        override val menuItems: List<MenuItem>,
        val width: DpOptional? = DpOptional(240.dp),
        val height: DpOptional? = null,
    ) : MenuItemGroup()

//    data class MenuItemCustom(
//        val tag: Any?,
//        val content: @Composable (Modifier, MenuItemCustom) -> Unit,
//    ): MenuItem

    @Immutable
    data class MenuItemSpacer(
        val width: DpOptional?,
        val height: DpOptional?,
    ) : MenuItem()

    @Immutable
    data class MenuItemContainer(
        val menuItem: MenuItem,
        val menuItemAlignment: Alignment? = Alignment.Center,
        val width: DpOptional? = null,
        val height: DpOptional? = null,
        val padding: Padding? = null,
        val onClick: ViewEventHandler? = null,
    ) : MenuItem()

    @Immutable
    data class MenuItemProgressButton(
        val overlayText: Text,
        val progress: Float,
        val width: DpOptional? = null,
        val height: DpOptional? = null,
        val onClick: ViewEventHandler? = null,
        val shapeSpec: ShapeSpec? = null,
    ) : MenuItem()

    @Immutable
    data object MenuItemDivider : MenuItem()

    @Immutable
    data class MenuItemViewStateWrapper<T : ViewState>(
        val viewState: T,
        val width: DpOptional? = null,
        val height: DpOptional? = null,
        val tintColor: ColorToken? = null,
    ) : MenuItem()
}

fun MenuItem.itemWidth(): DpOptional? {
    return when (this) {
        is MenuItem.MenuItemLabel -> (width as? Width.WidthDp)?.dp?.let { DpOptional(it) }
        is MenuItem.MenuItemIcon -> width
        is MenuItem.MenuItemButton -> width
        is MenuItem.MenuItemGroupVertical -> width
        is MenuItem.MenuItemContainer -> width
        is MenuItem.MenuItemProgressButton -> width
        else -> null
    }
}

fun MenuItem.itemHeight(): DpOptional? {
    return when (this) {
        is MenuItem.MenuItemLabel -> (height as? Height.HeightDp)?.dp?.let { DpOptional(it) }
        is MenuItem.MenuItemIcon -> height
        is MenuItem.MenuItemButton -> height
        is MenuItem.MenuItemGroupVertical -> height
        is MenuItem.MenuItemContainer -> height
        is MenuItem.MenuItemProgressButton -> height
        else -> null
    }
}