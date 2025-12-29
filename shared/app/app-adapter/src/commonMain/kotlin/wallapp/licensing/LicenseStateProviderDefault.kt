package wallapp.licensing

import wallapp.appstate.AppState
import wallapp.license.LicenseStateProvider

class LicenseStateProviderDefault(
    private val appState: AppState,
) : LicenseStateProvider {
    override val appInstallTime: Long
        get() = appState.appInstallTime
    override val doubleLicenseCheckFinished: Boolean
        get() = appState.doubleLicenseCheckFinished.value
}