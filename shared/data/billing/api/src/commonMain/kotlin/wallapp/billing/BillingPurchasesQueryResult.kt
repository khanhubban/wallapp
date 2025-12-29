package wallapp.billing

import wallapp.billing.purchase.BillingPurchases

sealed interface BillingPurchasesQueryResult {
    data class Success(val billingPurchases: BillingPurchases?) : BillingPurchasesQueryResult
    data class Error(val message: String) : BillingPurchasesQueryResult
}

val BillingPurchasesQueryResult.billingPurchases: BillingPurchases?
    get() = when (this) {
        is BillingPurchasesQueryResult.Success -> billingPurchases
        is BillingPurchasesQueryResult.Error -> null
    }