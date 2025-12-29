package wallapp.billing.debug.definitions

import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSku

interface BillingDebugSkuDefinitions {

    fun getBillingSkus(productIds: List<BillingProductId>): List<BillingSku>?
}