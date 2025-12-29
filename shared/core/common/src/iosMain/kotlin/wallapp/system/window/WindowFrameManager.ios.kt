package wallapp.system.window

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGSize
import platform.UIKit.UIScreen
import wallapp.log.Log
import wallapp.system.unit.SystemUnitManager

@OptIn(ExperimentalForeignApi::class)
class WindowFrameManagerIos(
    systemUnitManager: SystemUnitManager,
    coroutineScopeMain: CoroutineScope,
) : WindowFrameManagerDefault(systemUnitManager, coroutineScopeMain) {

    /**
     * Note: this has been somewhat unreliable, returning 0 for both width and height when valid
     * results are expected. Be cautious when using this method.
     *
     * For now, values are set via [wallapp.pixel.system.window.updateSize] during Compose.
     */
    override fun updateSize() {
        val main: UIScreen = UIScreen.mainScreen()
        val scale: CGFloat = main.scale
        val bounds: CValue<CGRect> = main.bounds
        val screenSizeInPoints: CGSize = bounds.useContents { this.size }
        val deviceWidthPx: CGFloat = screenSizeInPoints.width * scale
        val deviceHeightPx: CGFloat = screenSizeInPoints.height * scale
        Log.d("[WindowFrameManagerIos] deviceWidthPx: $deviceWidthPx, deviceHeightPx: $deviceHeightPx")
        setSize(deviceWidthPx.toInt(), deviceHeightPx.toInt())
    }

    init {
        updateSize()
    }
}