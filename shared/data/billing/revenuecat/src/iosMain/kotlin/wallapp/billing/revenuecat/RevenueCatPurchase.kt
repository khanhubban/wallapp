package wallapp.billing.revenuecat

import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.billing.purchase.BillingPurchases

@SealedInterop.Enabled
sealed class RevenueCatPurchase {
    data class Success(val purchases: BillingPurchases) : RevenueCatPurchase()
    data class RevenueCatError(val message: String, val userCancelled: Boolean) : RevenueCatPurchase()
    data object UnknownError : RevenueCatPurchase()
}