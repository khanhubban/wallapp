package wallapp.billing.revenuecat

import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.billing.sku.BillingSku

@SealedInterop.Enabled
sealed class RevenueCatBillingSkus {

    data class Success(
        val allBillingSkus: List<BillingSku>,
    ) : RevenueCatBillingSkus()

    data class RevenueCatError(val error: String) : RevenueCatBillingSkus() {
        val message: String
            get() = error
    }

    data object UnknownError : RevenueCatBillingSkus()
}