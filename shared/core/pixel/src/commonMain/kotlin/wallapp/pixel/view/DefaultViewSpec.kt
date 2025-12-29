package wallapp.pixel.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface DefaultViewSpec : ViewSpec {

    val paddingSmall: Dp
    val paddingDefault: Dp
    val paddingLarge: Dp

    val iconButtonLayerSize: Dp

    val horizontalParallaxMaxScale: Float
    val verticalParallaxMaxScale: Float

    val screenScrimHeight: Dp

    val loadingImageSize: Dp
        get() = 120.dp
}
