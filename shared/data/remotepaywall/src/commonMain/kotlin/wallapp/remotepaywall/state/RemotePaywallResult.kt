package wallapp.remotepaywall.state

import wallapp.billing.sku.BillingProductId

sealed class RemotePaywallResult {
    data class Purchased(val productId: BillingProductId) : RemotePaywallResult()
    data object Declined : RemotePaywallResult()
    data object Restored : RemotePaywallResult()
}