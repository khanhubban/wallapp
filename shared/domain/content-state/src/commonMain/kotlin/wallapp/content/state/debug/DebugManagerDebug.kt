package wallapp.content.state.debug

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.apprestarter.AppRestarter
import wallapp.coroutine.collectIn
import wallapp.data.purchase.PurchaseRecordRepository
import wallapp.interop.InteropBridge
import wallapp.license.cache.LicenseCache
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.view.ViewEventHandler
import wallapp.prefs.DevicePreferenceStorage
import wallapp.prefs.PreferenceDefaults
import wallapp.settings.Settings
import wallapp.system.toast.ToastDisplayController

class DebugManagerDebug(
    private val accountManager: AccountManager,
    private val purchaseRecordRepository: PurchaseRecordRepository,
    private val licenseCache: LicenseCache,
    private val licenseSettings: Settings,
    private val userSettings: Settings,
    private val deviceSettings: Settings,
    private val devicePreferenceStorage: DevicePreferenceStorage,
    private val preferenceDefaults: PreferenceDefaults,
    private val appRestarter: AppRestarter,
    private val alertManager: AlertManager,
    private val toastDisplayController: ToastDisplayController,
    private val interopBridge: InteropBridge,
    private val coroutineScopeMain: CoroutineScope,
) : DebugManager {

    override fun forceSignOut() {
        coroutineScopeMain.launch {
            accountManager.signOut()
            toastDisplayController.showToast("Signed out")
        }
    }

    override fun resetAll() {
        coroutineScopeMain.launch {
            accountManager.signOut()

            licenseSettings.resetAll()
            userSettings.resetAll()
            deviceSettings.resetAll()

            interopBridge.resetAllData()

            toastDisplayController.showToast("Resetting app…")
            delay(200L)
            appRestarter.restartApp()
        }
    }

    override fun resetEntitlements() {
        coroutineScopeMain.launch {
            licenseSettings.resetAll()
            devicePreferenceStorage.debugBillingData.value = preferenceDefaults.debugBillingData.default()
            devicePreferenceStorage.debugLicenseState.update(preferenceDefaults.debugLicenseState.default())

            purchaseRecordRepository.resetAll()
            licenseCache.resetAllToDefault()

            toastDisplayController.showToast("Entitlements reset!")
//            delay(200L)
//            appRestarter.restartApp()
        }
    }

    override fun forceCrash() {
        val a: String? = null
        a!!.length
    }

    override val useDebugRewardAdCount: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.useDebugRewardAdCount

    override val useDebugBillingManager: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.useDebugBillingManager

    private fun showRestartDialog() {
        alertManager.show(
            AlertViewState(
                title = "Manual Restart Required",
                message = "You must manually close and restart the app for this change to take effect.",
                buttonPrimary = "Close App",
                buttonPrimaryOnClick = ViewEventHandler.createOnClick { forceCrash() },
                buttonSecondary = "Later",
                buttonSecondaryOnClick = null,
            )
        )
    }

    private fun performRestart() {
        coroutineScopeMain.launch {
            toastDisplayController.showToast("Restarting to apply change...")
            delay(200L)
            appRestarter.restartApp()
        }
    }

    private fun restartOrAlert() {
        // The restart behavior is causing issues on Android, so all platforms will use the alert
        // for now.
//        if (appRestarter.enabled) {
//            performRestart()
//        } else {
            showRestartDialog()
//        }
    }

    private fun <T> MutableStateFlow<T>.restartOrAlertOnChange() {
        drop(1)
            .collectIn(coroutineScopeMain) {
                restartOrAlert()
            }
    }

    private fun initialize() {
        devicePreferenceStorage.useDebugBillingManager.restartOrAlertOnChange()
        devicePreferenceStorage.enableNativeUiRendering.restartOrAlertOnChange()
        devicePreferenceStorage.enableFeedPaging.restartOrAlertOnChange()
    }

    init {
        initialize()
    }
}