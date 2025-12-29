package wallapp.license.cache

import wallapp.device.state.DeviceState
import wallapp.di.Lazy
import wallapp.settings.Settings

class LicenseCacheBackupDefault(
    private val settings: Lazy<Settings>,
    private val licenseCache: Lazy<LicenseCache>,
    deviceState: DeviceState,
) : LicenseCacheBackup {

    init {
        if (deviceState.isUserUnlocked) {
            init()
        } else {
            deviceState.registerForUserUnlock { init() }
        }
    }

    fun init() {
    }

}
