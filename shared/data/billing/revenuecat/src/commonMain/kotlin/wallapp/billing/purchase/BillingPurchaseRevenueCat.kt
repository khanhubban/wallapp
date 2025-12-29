package wallapp.billing.purchase

import wallapp.billing.sku.BillingSkuSpec

data class BillingPurchaseRevenueCat(
    override val id: BillingPurchaseId?,
    override val isPurchasePending: Boolean,
    override val skuSpecs: List<BillingSkuSpec>,
    override val debugString: String,
) : BillingPurchase {
    override val debugStringShort: String
        get() = debugString
}