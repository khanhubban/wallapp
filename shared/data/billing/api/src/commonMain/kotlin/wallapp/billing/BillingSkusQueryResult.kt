package wallapp.billing

import wallapp.billing.sku.BillingSku

sealed interface BillingSkusQueryResult {
    data class Success(val billingSkus: List<BillingSku>?) : BillingSkusQueryResult
    data class Error(val message: String) : BillingSkusQueryResult
}
