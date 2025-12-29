package wallapp.billing.purchase

import wallapp.billing.sku.BillingSkuSpec

interface BillingPurchase {
    val id: BillingPurchaseId?
    val isPurchasePending: Boolean
    val skuSpecs: List<BillingSkuSpec>
    val debugStringShort: String
    val debugString: String
}


fun List<BillingPurchase>?.toDebugString(useShortLabel: Boolean): String? {
    if (this.isNullOrEmpty()) return null

    val itemsString = joinToString(separator = if (useShortLabel) { ", " } else { ",\n" }) {
        if (useShortLabel) {
            it.debugStringShort
        } else {
            it.debugString
        }
    }
    return "size: $size, items: $itemsString"
}