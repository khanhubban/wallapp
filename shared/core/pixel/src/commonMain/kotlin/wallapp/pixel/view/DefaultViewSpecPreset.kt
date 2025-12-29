package wallapp.pixel.view

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
object DefaultViewSpecPreset : DefaultViewSpec {

    override val paddingSmall: Dp
        get() = 8.dp
    override val paddingDefault: Dp
        get() = 16.dp
    override val paddingLarge: Dp
        get() = paddingDefault * 2

    override val iconButtonLayerSize: Dp
        get() = 40.dp

    override val screenScrimHeight: Dp
        get() = 80.dp

    override val horizontalParallaxMaxScale: Float
        get() = 1.2f
    override val verticalParallaxMaxScale: Float
        get() = 1.2f
}