package wallapp.pixel.button

import androidx.compose.runtime.Immutable
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemLabel
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text.Companion.presetText
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class ButtonViewState(
    val menuItem: MenuItem,
    val animatedViewSpec: AnimatedViewSpec? = null,
    /**
     * AnimatedViewSpec's shape will be prioritized. If it is null, then this shape will be used.
     * Shape cannot be null.
     */
    val shapeSpec: ShapeSpec?,
    val buttonAppearance: ButtonAppearance = ButtonAppearance.Default,
    val containerColorToken: ColorToken? = null,
    val eventHandler: ViewEventHandler?,
) : ViewState {

    companion object {
        val Preset = ButtonViewState(
            menuItem = MenuItemLabel("Button".presetText, onClick = null),
            shapeSpec = null,
            animatedViewSpec = null,
            buttonAppearance = ButtonAppearance.Default,
            containerColorToken = null,
            eventHandler = null,
        )
    }
}
