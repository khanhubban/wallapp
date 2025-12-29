package wallapp.billing.sku

data class BillingSku(
    val productType: BillingProductType,
    val productId: BillingProductId,
    val title: String,
    val description: String,
    val priceLocalized: String,
    val priceNumerical: Float,
    val priceCurrencyCode: String,
    val nativeSku: Any?,
) {
    val billingSkuSpec: BillingSkuSpec
        get() = BillingSkuSpec(productId)
}