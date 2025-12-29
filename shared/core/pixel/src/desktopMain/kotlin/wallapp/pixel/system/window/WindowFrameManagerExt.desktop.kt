package wallapp.pixel.system.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import wallapp.system.window.WindowFrameManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun WindowFrameManager.updateSize() {
    val containerSize = LocalWindowInfo.current.containerSize
    val deviceWidthPx = containerSize.width
    val deviceHeightPx = containerSize.height
    setSize(
        deviceWidthPx = deviceWidthPx,
        deviceHeightPx = deviceHeightPx,
    )
}
