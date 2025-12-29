package wallapp.system.window

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.graphics.Color
import wallapp.log.Logger

private val Log = Logger("WindowManagerDefault", loggingEnabled = true)

class WindowManagerDefault(
    private val windowFrameManager: WindowFrameManager,
) : WindowManager {

    override val isReady: Boolean
        get() = windowFrameManager.isReady.value

    override val windowFrame: WindowFrame
        get() = requireNotNull(windowFrameManager.windowFrame.value)

    override val systemBarColors: MutableStateFlow<SystemBarColors> = MutableStateFlow(SystemBarColors.Preset)

    override fun setStatusBarColor(color: Color) {
        val updated = systemBarColors.value.copy(statusBarColor = color)
        val current = systemBarColors.value
        if (updated != current) {
            systemBarColors.value = updated
            Log.d("setStatusBarColor: $color")
        }
    }

    override fun setStatusBarDarkIcons(darkIcons: Boolean?) {
        val updated = systemBarColors.value.copy(darkStatusBarIcons = darkIcons)
        val current = systemBarColors.value
        if (updated != current) {
            systemBarColors.value = updated
            Log.d("setStatusBarDarkIcons: $darkIcons")
        }
    }

    override fun setNavigationBarColor(color: Color) {
        val updated = systemBarColors.value.copy(navigationBarColor = color)
        val current = systemBarColors.value
        if (updated != current) {
            systemBarColors.value = updated
            Log.d("setNavigationBarColor: $color")
        }
    }
}
