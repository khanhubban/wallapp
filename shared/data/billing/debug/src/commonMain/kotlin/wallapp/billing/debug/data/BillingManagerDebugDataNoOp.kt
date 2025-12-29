package wallapp.billing.debug.data

import kotlinx.coroutines.flow.MutableStateFlow

object BillingManagerDebugDataNoOp : BillingManagerDebugData {
    override val currentPurchases: MutableStateFlow<String> = MutableStateFlow("")
}