package wallapp.system.window

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.StateFlow

interface WindowFrameManager {

    val isReady: StateFlow<Boolean>

    // Will be null until set on the first render frame.
    val windowFrame: StateFlow<WindowFrame?>

    fun setInsets(statusBarHeight: Dp, navBarHeight: Dp)
    fun setSize(deviceWidthPx: Int, deviceHeightPx: Int)
}

