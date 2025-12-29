package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.state.collection.CollectionActionButtonViewSpec.BuyCollectionActionButtonViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemIcon
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState

@SealedInterop.Enabled
sealed class CollectionActionButtonViewState : ViewState {

    abstract val viewSpec: CollectionActionButtonViewSpec
    abstract val buttonViewState: ButtonViewState

    @Immutable
    data class GetCollectionViewState(
        override val viewSpec: GetCollectionActionButtonViewSpec,
        override val buttonViewState: ButtonViewState,
    ) : CollectionActionButtonViewState()

    @Immutable
    data class BuyCollectionViewState(
        override val viewSpec: BuyCollectionActionButtonViewSpec,
        override val buttonViewState: ButtonViewState,
    ) : CollectionActionButtonViewState()

    @Immutable
    data class DownloadProgressButton(
        override val viewSpec: DownloadProgressButtonViewSpec,
        override val buttonViewState: ButtonViewState, // ignore this as this is designed to work with 3 items only
        val minIcon: MenuItemIcon,
        val countLabel: Text,
        val maxLabel: Text,
        val progress: Float,
    ) : CollectionActionButtonViewState()

    fun getButtonItems(): Triple<MenuItemIcon, Text, Text> {
        require(buttonViewState.menuItem !is MenuItem.MenuItemProgressButton)

        val menuItems = when (val menuItem = buttonViewState.menuItem) {
            is MenuItem.MenuItemGroup -> {
                menuItem.menuItems
            }

            is MenuItem.MenuItemButton -> {
                (menuItem.button.menuItem as MenuItem.MenuItemGroup).menuItems
            }

            else -> {
                throw IllegalArgumentException("Expected MenuItemGroup or MenuItemButton, got $menuItem")
            }
        }.filter { it !is MenuItem.MenuItemSpacer }
        require(menuItems.size == 3) {
            "Expected 3 menu items, got ${menuItems.size}"
        }

        val image = (menuItems[0] as MenuItemIcon)
        val label = (menuItems[1] as MenuItem.MenuItemLabel).text
        val labelAlt = (menuItems[2] as MenuItem.MenuItemLabel).text
        return Triple(image, label, labelAlt)
    }

}