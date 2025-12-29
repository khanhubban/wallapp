package wallapp.entitlement

import wallapp.billing.purchase.BillingPurchase
import wallapp.billing.purchase.BillingPurchases
import wallapp.content.model.Id
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.data.entitlement.EntitlementState
import wallapp.data.purchase.CollectionBillingSkuSpec
import wallapp.data.purchase.Purchasable
import wallapp.license.state.LicenseStateType

object EntitlementArbitrator {

    fun List<WallpaperItem>.filterRewardUnlockedItems(
        rewardUnlocked: List<Id>,
    ): List<WallpaperItem> {
        val remixes = filterIsInstance<WallpaperRemix>()
            .filter { rewardUnlocked.contains(it.id) }
        return remixes
    }

    fun unlockedItems(
        allWallpaperItems: List<WallpaperItem>,
        isLicensed: Boolean?,
        purchasedItems: List<WallpaperItem>,
        rewardUnlockedItems: List<WallpaperItem>,
    ): List<WallpaperItem> {
        if (isLicensed == true) {
            return allWallpaperItems
        }

        return mutableListOf<WallpaperItem>().apply {
            addAll(purchasedItems)
            addAll(rewardUnlockedItems)
        }.distinct()
    }

    fun arbitrateEntitlementState(
        licenseStateType: LicenseStateType,
        isPurchased: Boolean,
        isRewardUnlocked: Boolean,
        isFree: Boolean,
        isInCollection: Boolean,
    ): EntitlementState {
        if (licenseStateType == LicenseStateType.Plus) {
            return EntitlementState.SubscriberPlus
        }

        if (isPurchased) {
            return EntitlementState.Purchased
        }

        if (licenseStateType == LicenseStateType.AdFree) {
            return EntitlementState.SubscriberAdFree
        }

        if (isRewardUnlocked) {
            return EntitlementState.UnlockedReward
        }

        if (isInCollection) {
            return EntitlementState.Locked
        }

        if (isFree) {
            return EntitlementState.UnlockedFreeHd
        }

        return EntitlementState.UnlockedFree
    }

    fun List<BillingPurchase>.mapToPurchasables(
        allPurchasables: List<Purchasable>,
    ): List<Purchasable> {
        return map { purchase -> purchase.skuSpecs.map { it.productId } }
            .flatten()
            .flatMap { billingProductId ->
                allPurchasables.filter { it.billingSkuSpec.productId == billingProductId }
            }
    }

    fun arbitrateAllVerifiedPurchases(
        purchases: BillingPurchases?,
        allPurchasables: List<Purchasable>?,
    ): List<Purchasable>? {
        if (purchases == null || purchases.size == 0 || allPurchasables.isNullOrEmpty()) {
            return null
        }

        val existingVerified: List<Purchasable>? = purchases.verifiedPurchases
            ?.mapToPurchasables(allPurchasables)
        val newlyVerifiedPurchases: List<Purchasable>? = purchases.unverifiedPurchases
            ?.mapToPurchasables(allPurchasables)

        val all: List<Purchasable> = (existingVerified ?: emptyList()) +
                (newlyVerifiedPurchases ?: emptyList())
        return all.ifEmpty { null }
    }

    fun arbitrateCollectionPurchases(
        purchases: BillingPurchases?,
        collectionBillingSkuSpecs: List<CollectionBillingSkuSpec>?,
    ): List<CollectionBillingSkuSpec> {
        if (purchases == null || purchases.size == 0 || collectionBillingSkuSpecs.isNullOrEmpty()) {
            return emptyList()
        }

        // IMPORTANT: Check for entitlements instead of the productId to support cross-platform purchases
        val verifiedSpecs = collectionBillingSkuSpecs.filter { spec ->
            purchases.verifiedPurchases?.any { purchase ->
                purchase.skuSpecs.map { it.productId.entitlementId }.contains(
                    spec.billingSkuSpec.productId.entitlementId
                )
            } == true
        }

        return verifiedSpecs
    }
}