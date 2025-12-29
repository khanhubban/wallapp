package wallapp.billing.revenuecat

import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.billing.purchase.BillingPurchases


@SealedInterop.Enabled
sealed class RevenueCatBillingPurchases {

    data class Success(
        val billingPurchases: BillingPurchases?,
    ) : RevenueCatBillingPurchases()

    data class RevenueCatError(val code: Int, val errorMessage: String) : RevenueCatBillingPurchases() {
        val message: String
            get() = errorMessage
    }

    data object UnknownError : RevenueCatBillingPurchases()
}