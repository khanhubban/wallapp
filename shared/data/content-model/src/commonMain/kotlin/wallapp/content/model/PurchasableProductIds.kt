package wallapp.content.model

import wallapp.billing.sku.BillingProductId

data class PurchasableProductIds(
    val appStoreProductId: BillingProductId,
    val googlePlayProductId: BillingProductId,
    val revenueCatEntitlementId: String,
) {
    fun contains(productId: BillingProductId): Boolean {
        return appStoreProductId == productId || googlePlayProductId == productId
    }

    companion object {
        val Preset = PurchasableProductIds(
            appStoreProductId = BillingProductId.from(productId = "placeholder_preset", entitlementId = "placeholder_preset"),
            googlePlayProductId = BillingProductId.from(productId = "placeholder_preset", entitlementId = "placeholder_preset"),
            revenueCatEntitlementId = "placeholder_preset",
        )
    }
}
