package wallapp.device

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentActivity
import wallapp.system.window.deviceSizePx

class DeviceSpecAndroid(
    val context: Context,
    private val uiControllerManager: UiControllerManager,
) : DeviceSpec {

    private val activity: Activity
        get() = uiControllerManager.currentActivity!!
    private val windowManager: WindowManager
        get() = activity.windowManager

    override val density: StateFlow<Float> by lazy {
        MutableStateFlow(context.resources.displayMetrics.density)
    }

    override val size: StateFlow<DeviceSize> by lazy {
        val (width, height) = windowManager.deviceSizePx(activity)
        MutableStateFlow(DeviceSize(width, height))
    }
}