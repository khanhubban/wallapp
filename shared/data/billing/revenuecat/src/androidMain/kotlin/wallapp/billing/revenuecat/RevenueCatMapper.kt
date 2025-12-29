package wallapp.billing.revenuecat

import com.revenuecat.purchases.EntitlementInfo
import com.revenuecat.purchases.EntitlementInfos
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.models.GoogleStoreProduct
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.googleProduct
import wallapp.billing.purchase.BillingPurchase
import wallapp.billing.purchase.BillingPurchaseId.BillingPurchaseIdRevenueCat
import wallapp.billing.purchase.BillingPurchaseRevenueCat
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingProductType
import wallapp.billing.sku.BillingSku
import wallapp.billing.sku.BillingSkuSpec

object RevenueCatMapper {

    val EntitlementInfo.billingProductId: BillingProductId
        get() = BillingProductId.from(productIdentifier, identifier, productPlanIdentifier)

    fun mapEntitlementInfosToBillingSkus(entitlementInfos: EntitlementInfos): BillingPurchases? {
        return mapEntitlementInfosToBillingSkus(entitlementInfos.all.values.toList())
    }

    fun mapEntitlementInfosToBillingSkus(entitlementInfos: List<EntitlementInfo>):
            BillingPurchases? {
        val purchaseMap = entitlementInfos
            .groupBy(
                keySelector = {
                    when {
                        !it.isActive -> "inactive"
                        it.verification.isVerified -> "verified"
                        else -> "unverified"
                    }
                },
                valueTransform = { mapEntitlementInfoToBillingSku(it) }
            )

        val verified = purchaseMap["verified"]?.ifEmpty { null }
        val unverified = purchaseMap["unverified"]?.ifEmpty { null }
        val inactive = purchaseMap["inactive"]?.ifEmpty { null }
        if (verified == null && unverified == null && inactive == null) {
            return null
        }
        return BillingPurchases(verified, unverified, inactive)
    }

    fun mapEntitlementInfoToBillingSku(entitlementInfo: EntitlementInfo): BillingPurchase {
        val purchaseId = mapEntitlementInfoToBillingPurchaseId(entitlementInfo)

        return BillingPurchaseRevenueCat(
            id = purchaseId,
            isPurchasePending = false,
            skuSpecs = mapEntitlementInfoToBillingSkuSpecs(entitlementInfo),
            debugString = "EntitlementInfo: ${entitlementInfo.identifier}",
        )
    }

    fun mapEntitlementInfoToBillingPurchaseId(entitlementInfo: EntitlementInfo): BillingPurchaseIdRevenueCat {
        return BillingPurchaseIdRevenueCat(
            productIdentifier = entitlementInfo.productIdentifier,
            productPlanIdentifier = entitlementInfo.productPlanIdentifier,
            originalPurchaseDateEpoch = entitlementInfo.originalPurchaseDate.time,
        )
    }

    fun mapEntitlementInfoToBillingSkuSpecs(entitlementInfo: EntitlementInfo): List<BillingSkuSpec> {
        return listOf(
            BillingSkuSpec(productId = entitlementInfo.billingProductId),
        )
    }

    fun mapOfferingsToBillingSkus(offerings: Offerings): List<BillingSku> {
        return mapOfferingsToBillingSkus(offerings.all.values.toList())
    }

    fun mapOfferingsToBillingSkus(offerings: List<Offering>): List<BillingSku> {
        return offerings
            .flatMap { mapOfferingToBillingSkus(it) }
            .distinct()
    }

    fun mapOfferingToBillingSkus(offering: Offering): List<BillingSku> {
        return offering.availablePackages.map { mapPackageToBillingSku(it) }
    }

    fun mapStoreProductsToBillingSkus(storeProducts: List<StoreProduct>): List<BillingSku> {
        return storeProducts.map { mapStoreProductToBillingSku(it) }
    }

    fun mapStoreProductToBillingSku(storeProduct: StoreProduct): BillingSku {
        return BillingSku(
            productType = mapProductType(storeProduct.type),
            productId = mapProductId(storeProduct.googleProduct!!),
            title = storeProduct.title,
            description = storeProduct.description,
            priceLocalized = storeProduct.price.formatted,
            priceNumerical = storeProduct.price.amountMicros.toFloat() / 1000000f,
            priceCurrencyCode = storeProduct.price.currencyCode,
            nativeSku = storeProduct,
        )
    }

    fun mapPackageToBillingSku(revenueCatPackage: Package) = BillingSku(
        productType = mapProductType(revenueCatPackage.product.type),
        productId = mapProductId(revenueCatPackage.product.googleProduct!!),
        title = revenueCatPackage.product.title,
        description = revenueCatPackage.product.description,
        priceLocalized = revenueCatPackage.product.price.formatted,
        priceNumerical = revenueCatPackage.product.price.amountMicros.toFloat() / 1000000f,
        priceCurrencyCode = revenueCatPackage.product.price.currencyCode,
        nativeSku = revenueCatPackage.product,
    )

    fun mapProductId(googleStoreProduct: GoogleStoreProduct): BillingProductId {
        return BillingProductId.from(
            productId = googleStoreProduct.productId,
            planId = googleStoreProduct.basePlanId,
        )
    }

    fun mapProductType(productType: ProductType): BillingProductType {
        return when (productType) {
            ProductType.INAPP -> BillingProductType.InApp
            ProductType.SUBS -> BillingProductType.Subscription
            ProductType.UNKNOWN -> throw IllegalArgumentException("Unknown/unsupported product type")
        }
    }
}