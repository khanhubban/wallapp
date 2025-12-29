package wallapp.system.window

import android.app.Activity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.log.Log
import wallapp.system.ui.controller.UiControllerManagerAndroid
import wallapp.system.ui.controller.currentActivity
import wallapp.system.unit.SystemUnitManager
import android.view.WindowManager as WindowManagerSystem


/**
 * Note: Proper support yet to be added for pre-Android 11.
 */
class WindowFrameManagerAndroid(
    private val uiControllerManager: UiControllerManagerAndroid,
    private val systemUnitManager: SystemUnitManager,
) : WindowFrameManager {

    private val _isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean>
        get() = _isReady

    private val _windowFrame by lazy {
        MutableStateFlow(createWindowFrame())
            .also {
                Log.d("WindowFrameManager - windowFrame: ${it.value}")
            }
    }
    override val windowFrame: StateFlow<WindowFrame>
        get() = _windowFrame

    private val activity: Activity
        get() = uiControllerManager.currentActivity!!
    private val windowManager: WindowManagerSystem
        get() = activity.windowManager

    private val statusBarHeight: Dp
        get() = activity.statusBarHeight(systemUnitManager)

    private val navigationBarHeight: Dp
        get() = activity.navigationBarHeight(systemUnitManager)

    override fun setInsets(statusBarHeight: Dp, navBarHeight: Dp) {
        _windowFrame.value = createWindowFrame(statusBarHeight, navBarHeight)
    }

    private val deviceSize: Pair<Int, Int>
        get() = windowManager.deviceSizePx(activity)

    fun updateSize() {
        val size = deviceSize
        setSize(size.first, size.second)
    }

    override fun setSize(deviceWidthPx: Int, deviceHeightPx: Int) {
        _windowFrame.value = createWindowFrame(
            statusBarHeight = statusBarHeight,
            navBarHeight = navigationBarHeight,
            deviceWidthPx = deviceWidthPx,
            deviceHeightPx = deviceHeightPx
        )
        _isReady.value = true
    }

    private fun createWindowFrame(
        statusBarHeight: Dp? = null,
        navBarHeight: Dp? = null,
        deviceWidthPx: Int? = null,
        deviceHeightPx: Int? = null
    ): WindowFrame {
        val calculatedStatusBarHeight = statusBarHeight ?: this.statusBarHeight
        val calculatedNavBarHeight = navBarHeight ?: this.navigationBarHeight
        val calculatedDeviceWidthPx = deviceWidthPx ?: deviceSize.first
        val calculatedDeviceHeightPx = deviceHeightPx ?: deviceSize.second

        return WindowFrame(
            statusBarHeight = calculatedStatusBarHeight,
            navigationBarHeight = calculatedNavBarHeight,
            deviceWidth = systemUnitManager.pxToDp(calculatedDeviceWidthPx),
            deviceHeight = systemUnitManager.pxToDp(calculatedDeviceHeightPx),
            deviceWidthPx = calculatedDeviceWidthPx,
            deviceHeightPx = calculatedDeviceHeightPx,
        )
    }
}