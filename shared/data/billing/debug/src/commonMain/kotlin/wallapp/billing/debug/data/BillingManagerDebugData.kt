package wallapp.billing.debug.data

import kotlinx.coroutines.flow.MutableStateFlow

interface BillingManagerDebugData {

    val currentPurchases: MutableStateFlow<String>
}

