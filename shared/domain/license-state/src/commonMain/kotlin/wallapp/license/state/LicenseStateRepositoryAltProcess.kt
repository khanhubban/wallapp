package wallapp.license.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.annotation.VisibleForTesting
import wallapp.coroutine.collectIn
import wallapp.device.DeviceId
import wallapp.device.state.DeviceState
import wallapp.di.Lazy
import wallapp.license.LicenseStateProvider
import wallapp.license.cache.LicenseCache
import wallapp.licensing.CheckLicenseStateResult
import wallapp.licensing.LICENSE_STATE_CHECKING
import wallapp.licensing.LICENSE_STATE_TENTATIVE_ALLOWED
import wallapp.licensing.LICENSE_STATE_UNKNOWN
import wallapp.licensing.LicenseInfo
import wallapp.licensing.isLicenseStateAllowedAny

class LicenseStateRepositoryAltProcess(
    private val licenseCache: Lazy<LicenseCache>,
    private val deviceId: DeviceId,
    private val provider: LicenseStateProvider,
    deviceState: DeviceState,
    private val coroutineScopeMain: CoroutineScope,
) : LicenseStateRepository {

    private val _licenseInfo = MutableStateFlow(LicenseInfo(LICENSE_STATE_TENTATIVE_ALLOWED))
    override val licenseInfo: StateFlow<LicenseInfo> get() = _licenseInfo

    private val _isLicensed = MutableStateFlow<Boolean>(true)
    override val isLicensed: StateFlow<Boolean> get() = _isLicensed

//    fun setIsLicenseVerified() {
//        _isLicensedVerified.value = _isLicensed.value == true && _requiresLicenseCheck.value == false
//    }

    init {
        if (deviceState.isUserUnlocked) {
            init()
        } else {
            deviceState.registerForUserUnlock { init() }
        }
    }

    private fun init() {
        licenseCache.get().cachedLicenseState.collectIn(coroutineScopeMain) {
            updateLicenseInfo(
                LicenseInfo(
                    if (it.contains(deviceId.bestDeviceId)) {
                        it.toLicenseState()
                    } else {
                        LICENSE_STATE_UNKNOWN
                    }
                )
            )
        }
    }

    private fun updateLicenseInfo(licenseInfo: LicenseInfo) {
//        Log.d("updateLicenseInfo=%s", licenseInfo)
        _licenseInfo.value = licenseInfo
        licenseInfo.apply {
            if (licenseState != LICENSE_STATE_CHECKING) {
                _isLicensed.value = isLicenseStateAllowedAny(licenseState)
            }
        }
    }

    override fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult {
        // ignore
        return CheckLicenseStateResult.NOT_CHECKING
    }

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) {
        // ignore
    }

    @VisibleForTesting internal fun String.toLicenseState(): Int = substring(deviceId.bestDeviceId.length + 1).toInt()
}