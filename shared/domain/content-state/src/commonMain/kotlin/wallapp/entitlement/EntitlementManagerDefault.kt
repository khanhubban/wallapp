package wallapp.entitlement

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import wallapp.billing.InAppPurchaseRepository
import wallapp.coroutine.collectIn
import wallapp.data.purchase.CollectionBillingSkuSpec
import wallapp.data.purchase.PurchaseRecordRepository
import wallapp.entitlement.EntitlementArbitrator.arbitrateCollectionPurchases
import wallapp.log.Log
import wallapp.purchase.PurchasableRepository
import wallapp.result.Result
import wallapp.userprofile.UserProfileRepository

class EntitlementManagerDefault(
    purchaseRecordRepository: PurchaseRecordRepository,
    purchasableRepository: PurchasableRepository,
    inAppPurchaseRepository: InAppPurchaseRepository,
    userProfileRepository: UserProfileRepository,
    coroutineScopeMain: CoroutineScope,
) : EntitlementManager {

    init {
        combine(
            // wait for user profile to be available as update happens in the profile
            userProfileRepository.currentUserProfile.filter { it is Result.Success },
            inAppPurchaseRepository.purchases,
            purchasableRepository.collectionPurchasableSkuSpecs,
        ) { currentUserProfile, purchases, collectionSkuSpecs ->
            Log.d("[Billing] [EntitlementManagerDefault] collectionSkuSpecs.size:  ${collectionSkuSpecs.size}, currentUserProfile: $currentUserProfile, purchases: $purchases")
            arbitrateCollectionPurchases(purchases, collectionSkuSpecs)
        }.collectIn(coroutineScopeMain) { purchasedCollectionSkuSpecs: List<CollectionBillingSkuSpec> ->
            Log.d("[Billing] [EntitlementManagerDefault] purchasedCollectionSkuSpecs.size: ${purchasedCollectionSkuSpecs.size}\n${purchasedCollectionSkuSpecs.joinToString(separator = "\n") { "  $it" }}")
            val purchasedIds = purchasedCollectionSkuSpecs.map { collectionSkuSpec ->
                collectionSkuSpec.collectionId
            }
            if (purchasedIds.isNotEmpty()) {
                purchaseRecordRepository.setAllPurchased(purchasedIds)
            }
        }
    }
}