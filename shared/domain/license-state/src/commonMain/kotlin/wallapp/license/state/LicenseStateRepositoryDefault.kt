package wallapp.license.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.annotation.VisibleForTesting
import wallapp.coroutine.collectIn
import wallapp.device.DeviceId
import wallapp.device.state.DeviceState
import wallapp.di.Lazy
import wallapp.license.cache.LicenseCache
import wallapp.licensing.CheckLicenseStateResult
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_STANDARD
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
import wallapp.licensing.LICENSE_STATE_CHECKING
import wallapp.licensing.LICENSE_STATE_NOT_ALLOWED
import wallapp.licensing.LICENSE_STATE_TENTATIVE_ALLOWED
import wallapp.licensing.LICENSE_STATE_UNKNOWN
import wallapp.licensing.LicenseInfo
import wallapp.licensing.LicenseRepository
import wallapp.licensing.isLicenseStateAllowedAny

class LicenseStateRepositoryDefault(
    private val licenseRepositoryLazy: Lazy<LicenseRepository>,
    private val licenseCache: Lazy<LicenseCache>,
    private val deviceId: DeviceId,
    deviceState: DeviceState,
    private val coroutineScopeMain: CoroutineScope,
) : LicenseStateRepository {

    private val licenseRepository: LicenseRepository
        get() = licenseRepositoryLazy.get()

    private val _licenseInfo = MutableStateFlow(LicenseInfo(LICENSE_STATE_TENTATIVE_ALLOWED))
    override val licenseInfo: StateFlow<LicenseInfo> get() = _licenseInfo

    private val _isLicensed = MutableStateFlow(false)
    @Deprecated("Use licenseInfo.licenseState instead")
    override val isLicensed: StateFlow<Boolean> get() = _isLicensed

    init {
        // We need to delay initialization of LicenseCache until user unlocks the device after
        // fresh boot up as it cannot use DirectBoot storage context. The ramifications of this
        // delay are that after a reboot the feature locking will not work as expected.
        // Features like FlickFX will be available even if they are supposed to be locked.
        if (deviceState.isUserUnlocked) {
            init()
        } else {
            deviceState.registerForUserUnlock { init() }
        }
    }

    private fun init() {
        coroutineScopeMain.launch {
            licenseRepository.licenseInfo.collect {
                if (it.licenseState in listOf(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED, LICENSE_STATE_ALLOWED_PLUS_STANDARD, LICENSE_STATE_NOT_ALLOWED)) {
                    licenseCache.get().cachedLicenseState.value = (constructCachedState(it.licenseState))
                }
                updateLicenseInfo(it)
            }
        }

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
        return licenseRepository.checkLicenseState(forceUpdate)
    }

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) {
        licenseRepository.setLicenseInfoState(licenseInfo)
    }

    @VisibleForTesting internal fun constructCachedState(licenseState: Int) = "${deviceId.bestDeviceId}.${licenseState}"

    @VisibleForTesting internal fun String.toLicenseState(): Int = substring(deviceId.bestDeviceId.length + 1).toInt()
}