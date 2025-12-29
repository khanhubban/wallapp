package wallapp.billing.data

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.billing.debug.data.BillingManagerDebugData
import wallapp.prefs.DevicePreferenceStorage

class BillingManagerDebugDataDebug(
    private val devicePreferenceStorage: DevicePreferenceStorage,
) : BillingManagerDebugData {

    override val currentPurchases: MutableStateFlow<String>
        get() = devicePreferenceStorage.debugBillingData
}