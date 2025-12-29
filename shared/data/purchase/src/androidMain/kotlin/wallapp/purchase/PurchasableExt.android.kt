package wallapp.purchase

import wallapp.billing.sku.BillingProductId
import wallapp.content.model.PurchasableProductIds

actual fun getPlatformBillingProductId(
    purchasableProductIds: PurchasableProductIds,
): BillingProductId = purchasableProductIds.googlePlayProductId

