package wallapp.system.window

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class WindowFrame(
    val windowInsets: WindowInsets,
    val windowSize: WindowSize,
) {
    constructor(
        statusBarHeight: Dp,
        navigationBarHeight: Dp,
        deviceWidth: Dp,
        deviceHeight: Dp,
        deviceWidthPx: Int,
        deviceHeightPx: Int,
    ) : this(
        windowInsets = WindowInsets(
            statusBarHeight = statusBarHeight,
            navigationBarHeight = navigationBarHeight,
        ),
        windowSize = WindowSize(
            deviceWidth = deviceWidth,
            deviceHeight = deviceHeight,
            deviceWidthPx = deviceWidthPx,
            deviceHeightPx = deviceHeightPx,
        ),
    )

    val statusBarHeight: Dp
        get() = windowInsets.statusBarHeight
    val navigationBarHeight: Dp
        get() = windowInsets.navigationBarHeight
    val deviceWidth: Dp
        get() = windowSize.deviceWidth
    val deviceHeight: Dp
        get() = windowSize.deviceHeight
    val deviceWidthPx: Int
        get() = windowSize.deviceWidthPx
    val deviceHeightPx: Int
        get() = windowSize.deviceHeightPx

    companion object {
        val Preset = WindowFrame(
            windowInsets = WindowInsets.Preset,
            windowSize = WindowSize.Preset,
        )

        val PresetIphoneSe = WindowFrame(
            windowInsets = WindowInsets(
                statusBarHeight = 20.0.dp,
                navigationBarHeight = 0.0.dp,
            ),
            windowSize = WindowSize(
                deviceWidth = 375.0.dp,
                deviceHeight = 667.0.dp,
                deviceWidthPx = 750,
                deviceHeightPx = 1334,
            ),
        )

        val PresetIphone15 = WindowFrame(
            windowInsets = WindowInsets(statusBarHeight = 59.0.dp, navigationBarHeight = 34.0.dp),
            windowSize = WindowSize(
                deviceWidth = 393.0.dp,
                deviceHeight = 852.0.dp,
                deviceWidthPx = 1179,
                deviceHeightPx = 2556
            ),
        )

        // A slightly larger version of the iPhone SE is considered "compact".
        val PresetCompat = PresetIphoneSe.copy(
            windowSize = PresetIphoneSe.windowSize.copy(
                deviceHeight = 740.dp,
                deviceHeightPx = 740 * 2,
            )
        )
    }

}
