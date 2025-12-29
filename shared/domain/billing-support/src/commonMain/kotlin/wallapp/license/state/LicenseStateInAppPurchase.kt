package wallapp.license.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.account.AccountManager
import wallapp.device.state.DeviceState
import wallapp.inappupdate.InAppUpdateChecker
import wallapp.instantapp.InstantAppManager
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_STANDARD
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
import wallapp.log.Log
import wallapp.util.combine


class LicenseStateInAppPurchase(
    private val licenseStateRepository: LicenseStateRepository,
    private val inAppUpdateChecker: InAppUpdateChecker,
    private val instantAppManager: InstantAppManager,
    private val accountManager: AccountManager,
    private val licenseStateSessionManager: LicenseStateSessionManager,
    deviceState: DeviceState,
    coroutineScopeMain: CoroutineScope,
) : LicenseState() {

    override val licenseStateType: StateFlow<LicenseStateType> = combine(
        licenseStateRepository.licenseInfo,
        inAppUpdateChecker.appUpdateRequired,
        accountManager.signedInAccount,
        licenseStateSessionManager.granted,
    ) { _, _, _, licenseStateSessionManagerGranted ->
        updateIsPurchased(licenseStateSessionManagerGranted = licenseStateSessionManagerGranted)
    }.stateIn(coroutineScopeMain, SharingStarted.Eagerly, LicenseStateType.Unlicensed)

    private val _state = MutableStateFlow<Int?>(null)
    override val state: StateFlow<Int?>
        get() = _state

    private fun updateIsPurchased(licenseStateSessionManagerGranted: Boolean): LicenseStateType {
        val currentAccount = accountManager.signedInAccount.value
        val licenseState = licenseStateRepository.licenseInfo.value.licenseState
        val appUpdateRequired = inAppUpdateChecker.appUpdateRequired.value
        val isInstantApp = instantAppManager.isInstantApp

        val licenseStateType = when {
            (licenseState == LICENSE_STATE_ALLOWED_PLUS_UNLIMITED && !appUpdateRequired)
                    || isInstantApp
                    || currentAccount?.hasSpecialCasePlusEntitlement == true
                    || licenseStateSessionManagerGranted -> {
                LicenseStateType.Plus
            }
            licenseState == LICENSE_STATE_ALLOWED_PLUS_STANDARD -> {
                LicenseStateType.AdFree
            }
            else -> {
                LicenseStateType.Unlicensed
            }
        }
        _state.value = mapToState(licenseStateType.isLicensedAny(), licenseState)

        Log.d("[Billing] updateIsPurchased() result: $licenseStateType, " +
                "isLicensedAny: ${licenseStateType.isLicensedAny()}, " +
                "appUpdateRequired: $appUpdateRequired"
        )
//        _isLicensed.updateValueIfNew(isLicensed)
        return licenseStateType
    }

    init {
        if (deviceState.isUserUnlocked) {
            init()
        } else {
            deviceState.registerForUserUnlock { init() }
        }
    }

    fun init() {
        updateIsPurchased(licenseStateSessionManagerGranted = false)
    }
}
