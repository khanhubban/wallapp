package wallapp.purchase

import wallapp.billing.sku.BillingProductId
import wallapp.content.model.PurchasableProductIds

/**
 * TODO: Add proper Desktop ProductId.
 */
actual fun getPlatformBillingProductId(
    purchasableProductIds: PurchasableProductIds,
): BillingProductId = purchasableProductIds.googlePlayProductId

