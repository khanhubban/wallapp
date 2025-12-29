package wallapp.content.state.upgrade.plus.button

import androidx.compose.runtime.Immutable
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class UpgradeButtonViewState(
    val indicator: PlusIndicatorViewState,
    val label: MenuItem,
    val viewEventHandler: ViewEventHandler,
    /**
     * If true, the button will be rendered with a larger size. Intended for SignUp screen.
     */
    val heroSize: Boolean,
    val shapeSpec: ShapeSpec,
) : ViewState
