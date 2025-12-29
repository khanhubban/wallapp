package wallapp.system.window

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class WindowSize(
    val deviceWidth: Dp,
    val deviceHeight: Dp,
    val deviceWidthPx: Int,
    val deviceHeightPx: Int,
) {
    companion object {
        val Preset = WindowSize(
            deviceWidth = 360.dp,
            deviceHeight = 640.dp,
            deviceWidthPx = 360 * 2,
            deviceHeightPx = 640 * 2,
        )
    }
}
