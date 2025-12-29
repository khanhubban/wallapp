package wallapp.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.power.PowerManager
import wallapp.system.platform.PlatformFeature
import wallapp.system.ui.mode.UiModeManager

class SystemThemeAndroid(
    private val uiModeManager: UiModeManager,
    private val powerManager: PowerManager,
) : SystemTheme {

    override val isDarkTheme: Boolean
        get() {
            if (uiModeManager.isNightModeDisplaying) {
                return true
            }

            // As per Google's recommendation, enable dark theme if the device is in
            // power saving mode for pre-API 29 devices
            if (!PlatformFeature.SystemDarkTheme) {
                return powerManager.isPowerSaveMode()
            }

            return false
        }

    private val _darkTheme by lazy { MutableStateFlow(isDarkTheme) }
    override val darkTheme: StateFlow<Boolean>
        get() = _darkTheme

    fun update() {
        _darkTheme.value = isDarkTheme
    }
}