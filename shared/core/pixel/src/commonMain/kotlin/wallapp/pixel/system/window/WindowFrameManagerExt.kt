package wallapp.pixel.system.window

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import wallapp.pixel.compose.pxToDp
import wallapp.system.window.WindowFrameManager

@Composable
fun WindowFrameManager.updateFrame() {
    updateSize()
    updateInsets()
}

/**
 * Note: not currently implemented on Android
 */
@Composable
expect fun WindowFrameManager.updateSize()

@Composable
fun WindowFrameManager.updateInsets() {
    setInsets(
        statusBars = WindowInsets.statusBars,
        navigationBars = WindowInsets.navigationBars,
    )
}

@Composable
fun WindowFrameManager.setInsets(
    statusBars: WindowInsets,
    navigationBars: WindowInsets,
) {
    val density = LocalDensity.current
    setInsets(
        statusBarHeight = statusBars.getTop(density = density).pxToDp(),
        navBarHeight = navigationBars.getBottom(density = density).pxToDp(),
    )
}