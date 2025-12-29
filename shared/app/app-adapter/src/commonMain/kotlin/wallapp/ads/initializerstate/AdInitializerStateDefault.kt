package wallapp.ads.initializerstate

import wallapp.instantapp.InstantAppManager
import wallapp.license.state.LicenseState
import wallapp.license.state.isLicensedAny
import wallapp.log.Log
import wallapp.process.Process


class AdInitializerStateDefault(
    private val process: Process,
    private val licenseState: LicenseState,
    private val instantAppManager: InstantAppManager,
): AdInitializerState {

    override val initializeAds: Boolean
        get() = if (!process.isDefaultProcess) {
            Log.d("[AdMob] initializeAds: false, reason: not default process")
            false
        } else if (licenseState.licenseStateType.value.isLicensedAny()) {
            Log.d("[AdMob] initializeAds: false, reason: isLicensed")
            false
        } else if (instantAppManager.isInstantApp) {
            Log.d("[AdMob] initializeAds: false, reason: instantApp")
            false
        } else {
            Log.d("[AdMob] initializeAds: true, reason: not purchased and main process")
            true
        }

}