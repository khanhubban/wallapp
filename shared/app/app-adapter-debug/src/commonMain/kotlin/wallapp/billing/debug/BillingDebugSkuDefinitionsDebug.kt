package wallapp.billing.debug

import wallapp.billing.debug.definitions.BillingDebugSkuDefinitions
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSku
import wallapp.data.collection.CollectionPurchasable
import wallapp.entitlement.EntitlementSkuSpecs
import wallapp.purchase.platformBillingProductId

class BillingDebugSkuDefinitionsDebug(
    private val entitlementSkuSpecs: EntitlementSkuSpecs,
//    private val contentRepository: ContentRepository,
//    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
): BillingDebugSkuDefinitions {

    private val subscriptionAnnual by lazy {
        BillingSkuDebugFactory.annualSubscription(
            productId = entitlementSkuSpecs.plusAnnualBillingSkuSpec.value.productId,
        )
    }

    private val subscriptionMonthly by lazy {
        BillingSkuDebugFactory.monthlySubscription(
            productId = entitlementSkuSpecs.plusMonthlyBillingSkuSpec.value.productId,
        )
    }

    private val adFreeMonthly by lazy {
        BillingSkuDebugFactory.adFreeMonthlySubscription(
            productId = entitlementSkuSpecs.adFreeMonthlyBillingSkuSpec.value.productId,
        )
    }

    private val CollectionPurchasable.skuDetails: List<BillingSku>
        get() = listOf(
            BillingSkuDebugFactory.collectionIap(
                productId = purchasableProductIds.platformBillingProductId,
                collectionName = label,
                artistName = "",
            ),
        )
    private val BillingProductId.asCollectionBillingSku: BillingSku
        get() = BillingSkuDebugFactory.collectionIap(
            productId = this,
            collectionName = "",
            artistName = "",
        )
    private val BillingProductId.isCollectionSku: Boolean
        get() = this.productId.contains("iap.coll")

//    private val collectionPurchasables: StateFlow<List<CollectionPurchasable>?> =
//        contentRepository.collectionPurchasables
//            .stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = null)

    /**
     * Constructs a [BillingSku] for the given [productId]. Can't fetch from [PurchasableRepository]
     * or similar because that is not populated in time when using [BillingManagerDebug].
     */
    private fun getBillingSku(productId: BillingProductId): BillingSku? {
        return when {
            productId == entitlementSkuSpecs.plusAnnualBillingSkuSpec.value.productId -> {
                subscriptionAnnual
            }
            productId == entitlementSkuSpecs.plusMonthlyBillingSkuSpec.value.productId -> {
                subscriptionMonthly
            }
            productId == entitlementSkuSpecs.adFreeMonthlyBillingSkuSpec.value.productId -> {
                adFreeMonthly
            }
            productId.isCollectionSku -> {
                productId.asCollectionBillingSku
            }
            else -> {
                null
            }
        }
    }

    override fun getBillingSkus(productIds: List<BillingProductId>): List<BillingSku>? {
        return productIds
            .mapNotNull { getBillingSku(it) }
            .ifEmpty { null }
    }
}
