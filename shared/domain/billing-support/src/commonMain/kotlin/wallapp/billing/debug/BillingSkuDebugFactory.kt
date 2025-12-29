package wallapp.billing.debug

import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingProductType
import wallapp.billing.sku.BillingSku

object BillingSkuDebugFactory {

    fun billingSku(
        productId: BillingProductId,
        productType: BillingProductType = BillingProductType.InApp,
        priceLocalized: String,
        priceNumerical : Float,
        priceCurrencyCode: String = "USD",
        title: String,
        description: String,
    ): BillingSku = BillingSku(
        productId = productId,
        productType = productType,
        priceLocalized = priceLocalized,
        priceNumerical = priceNumerical,
        priceCurrencyCode = priceCurrencyCode,
        title = title,
        description = description,
        nativeSku = null,
    )

    private const val recurringSubscriptionDescription = "Unlock all wallpapers"

    fun annualSubscription(productId: BillingProductId): BillingSku {
        return billingSku(
            productId = productId,
            priceLocalized = "$49.91",
            priceNumerical = 49.91f,
            title = "Plus (annual)",
            description = recurringSubscriptionDescription,
        )
    }

    fun monthlySubscription(productId: BillingProductId): BillingSku {
        return billingSku(
            productId = productId,
            priceLocalized = "$11.91",
            priceNumerical = 11.91f,
            title = "Plus (monthly)",
            description = recurringSubscriptionDescription,
        )
    }

    fun adFreeMonthlySubscription(productId: BillingProductId): BillingSku {
        return billingSku(
            productId = productId,
            priceLocalized = "$1.99",
            priceNumerical = 2.91f,
            title = "Plus Ad-Free",
            description = "Remove all ads",
        )
    }

    fun collectionIap(
        productId: BillingProductId,
        collectionName: String,
        artistName: String,
    ): BillingSku {
        return billingSku(
            productId = productId,
            productType = BillingProductType.InApp,
            priceLocalized = "$2.99",
            priceNumerical = 2.99f,
            priceCurrencyCode = "USD",
            title = "$collectionName Collection",
            description = "Unlock $artistName's collection",
        )
    }
}