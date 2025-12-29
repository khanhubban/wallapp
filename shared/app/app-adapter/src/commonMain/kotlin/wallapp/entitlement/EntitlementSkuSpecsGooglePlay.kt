package wallapp.entitlement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId
import wallapp.data.content.ContentRepository

class EntitlementSkuSpecsGooglePlay(
    private val contentRepository: ContentRepository,
) : EntitlementSkuSpecs {

    override val plusMonthlyBillingSkuSpec: StateFlow<BillingSkuSpec> = MutableStateFlow(
        BillingSkuSpec(
            BillingProductId.from(
                productId = "com.example.sub.monthly.tier1",
                planId = "wallapp-sub-monthly-tier1-base",
                entitlementId = "Plus"
            )
        )
    )

    override val plusAnnualBillingSkuSpec: StateFlow<BillingSkuSpec> = MutableStateFlow(
        BillingSkuSpec(
            BillingProductId.from(
                productId = "com.example.sub.annual.tier1",
                planId = "wallapp-sub-annual-tier1-base",
                entitlementId = "Plus"
            )
        )
    )

    override val adFreeMonthlyBillingSkuSpec: StateFlow<BillingSkuSpec> = MutableStateFlow(
        BillingSkuSpec(
            BillingProductId.from(
                productId = "com.example.sub.monthly.adfree",
                planId = "wallapp-sub-monthly-adfree-base",
                entitlementId = "Plus Ad Free"
            )
        )
    )

    override val collectionSkuSpecs: Flow<List<BillingSkuSpec>?> =
        contentRepository.collectionPurchasables.map { collections ->
            collections.map { collection ->
                BillingSkuSpec(collection.purchasableProductIds.googlePlayProductId)
            }.ifEmpty { null }
        }

    override fun getCollectionSkuSpec(collectionId: CollectionId): Flow<BillingSkuSpec?> =
        contentRepository.getCollectionPurchasable(collectionId).map { collection ->
            collection?.let { BillingSkuSpec(it.purchasableProductIds.googlePlayProductId) }
        }
}