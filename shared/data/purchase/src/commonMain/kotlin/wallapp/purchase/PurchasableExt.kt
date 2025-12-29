package wallapp.purchase

import wallapp.billing.sku.BillingProductId
import wallapp.content.model.PurchasableProductIds

expect fun getPlatformBillingProductId(purchasableProductIds: PurchasableProductIds): BillingProductId

val PurchasableProductIds.platformBillingProductId: BillingProductId
    get() = getPlatformBillingProductId(this)