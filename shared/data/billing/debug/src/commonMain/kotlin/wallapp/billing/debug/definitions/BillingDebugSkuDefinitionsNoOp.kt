package wallapp.billing.debug.definitions

import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSku

object BillingDebugSkuDefinitionsNoOp : BillingDebugSkuDefinitions {
    override fun getBillingSkus(productIds: List<BillingProductId>): List<BillingSku>? = null
}
