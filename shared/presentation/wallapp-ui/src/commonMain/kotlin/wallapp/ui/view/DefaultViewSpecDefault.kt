package wallapp.ui.view

import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.DefaultViewSpec
import wallapp.view.ViewSpecArbitrator

class DefaultViewSpecDefault(
    private val viewSpecArbitrator: ViewSpecArbitrator,
): DefaultViewSpec {

    override val paddingSmall: Dp
        get() = viewSpecArbitrator.paddingSmall
    override val paddingDefault: Dp
        get() = viewSpecArbitrator.paddingDefault
    override val paddingLarge: Dp
        get() = viewSpecArbitrator.paddingLarge

    override val iconButtonLayerSize: Dp
        get() = viewSpecArbitrator.iconButtonLayerSize

    override val horizontalParallaxMaxScale: Float
        get() = 1.2f

    override val verticalParallaxMaxScale: Float
        get() = 1.05f

    override val screenScrimHeight: Dp
        get() = viewSpecArbitrator.screenScrimHeight
}