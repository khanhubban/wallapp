package wallapp.purchase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.billing.BillingManager
import wallapp.billing.BillingSkusQueryResult
import wallapp.billing.sku.BillingSku
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId
import wallapp.data.collection.CollectionPurchasable
import wallapp.data.content.ContentRepository
import wallapp.data.purchase.CollectionBillingSkuSpec
import wallapp.data.purchase.Purchasable
import wallapp.entitlement.EntitlementSkuSpecs
import wallapp.log.Logger
import wallapp.util.combine

class PurchasableRepositoryDefault(
    private val entitlementSkuSpecs: EntitlementSkuSpecs,
    private val billingManager: BillingManager,
    private val contentRepository: ContentRepository,
    private val purchasableMapper: PurchasableMapper,
    coroutineScopeIo: CoroutineScope,
) : PurchasableRepository {

    companion object {
        val Log = Logger("EntitlementSkusDefault")
    }

    private val _collectionPurchasables: StateFlow<List<CollectionPurchasable>?> by lazy {
        contentRepository.collectionPurchasables
            .stateIn(coroutineScopeIo, started = SharingStarted.Eagerly, initialValue = null)
    }

    private val monthlyProductId: Flow<BillingSkuSpec>
        get() = entitlementSkuSpecs.plusMonthlyBillingSkuSpec
    private val yearlyProductId: Flow<BillingSkuSpec>
        get() = entitlementSkuSpecs.plusAnnualBillingSkuSpec
    private val adFreeMonthlyProductId: Flow<BillingSkuSpec>
        get() = entitlementSkuSpecs.adFreeMonthlyBillingSkuSpec
    private val collectionProductIds: Flow<List<BillingSkuSpec>?>
        get() = entitlementSkuSpecs.collectionSkuSpecs

    override val plusPurchasableSkuSpecs: StateFlow<List<BillingSkuSpec>> = combine(
        monthlyProductId,
        yearlyProductId,
    ) { monthly, yearly ->
        listOfNotNull(monthly, yearly)
    }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, emptyList())

    override val adFreePurchasableSkuSpecs: StateFlow<List<BillingSkuSpec>> =
        adFreeMonthlyProductId.map { listOfNotNull(it) }
            .stateIn(coroutineScopeIo, SharingStarted.Eagerly, emptyList())

    override val collectionPurchasableSkuSpecs: StateFlow<List<CollectionBillingSkuSpec>> =
        contentRepository.collectionPurchasables.mapNotNull { collections ->
            collections.map { collection ->
                CollectionBillingSkuSpec(
                    collection.id,
                    BillingSkuSpec(collection.purchasableProductIds.platformBillingProductId)
                )
            }
        }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, emptyList())

    private fun <T> getBillingSku(
        productId: BillingSkuSpec,
        map: (BillingSku) -> T,
    ): Flow<T?> =
        flow {
            val result: BillingSkusQueryResult = billingManager
                .queryBillingSkus(listOf(productId.productId))
            if (result is BillingSkusQueryResult.Success) {
                result.billingSkus?.firstOrNull()?.also {
                    Log.d("getBillingSku($productId): ${it.billingSkuSpec}")
                    emit(map(it))
                }
            } else {
                emit(null)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val plusUnlimitedMonthlyPurchasable: StateFlow<Purchasable.SubscriptionUnlimitedMonthly?> =
        monthlyProductId
            .flatMapLatest { billingSkuSpec ->
                getBillingSku(billingSkuSpec) {
                    purchasableMapper.mapPurchasableSubscriptionUnlimitedMonthly(it)
                }
            }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val plusUnlimitedAnnualPurchasable: StateFlow<Purchasable.SubscriptionUnlimitedAnnual?> =
        combine(yearlyProductId, plusUnlimitedMonthlyPurchasable) { billingSkuSpec, plusMonthly ->
            billingSkuSpec to plusMonthly
        }
            .flatMapLatest { data ->
                val (billingSkuSpec, plusUnlimitedMonthlyPurchasable: Purchasable.SubscriptionUnlimitedMonthly?) = data
                getBillingSku(billingSkuSpec) { annualBillingSku ->
                    purchasableMapper
                        .mapPurchasableSubscriptionUnlimitedAnnual(annualBillingSku, plusUnlimitedMonthlyPurchasable)
                }
            }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val plusStandardMonthlyPurchasable: StateFlow<Purchasable.SubscriptionStandardMonthly?> =
        adFreeMonthlyProductId
            .flatMapLatest { billingSkuSpec ->
                getBillingSku(billingSkuSpec) {
                    purchasableMapper.mapPurchasableSubscriptionAdFreeMonthly(it)
                }
            }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, null)

    override val plusPurchasables: StateFlow<List<Purchasable.Subscription>?> = combine(
        plusUnlimitedMonthlyPurchasable,
        plusUnlimitedAnnualPurchasable
    ) { plusMonthly, plusAnnual ->
        listOfNotNull(plusMonthly, plusAnnual)
            .ifEmpty { null }
    }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, null)

    override val collectionPurchasables: StateFlow<List<Purchasable.Collection>?> by lazy {
        _collectionPurchasables.map { collections: List<CollectionPurchasable>? ->
            if (collections == null) {
                return@map null
            }
            val productIds = collections.map { it.purchasableProductIds.platformBillingProductId }
            if (productIds.isEmpty()) {
                return@map null
            }

            billingManager.queryBillingSkus(productIds)
                .let {
                    if (it is BillingSkusQueryResult.Success) {
                        it.billingSkus
                    } else {
                        null
                    }
                }
                ?.mapNotNull { billingSku ->
                    val collection = collections
                        .find { it.purchasableProductIds.contains(billingSku.productId) }
                    if (collection != null) {
                        Purchasable.Collection(collection.id, billingSku)
                    } else {
                        null
                    }
                }
                ?.ifEmpty { null }
        }
            .onEach { items ->
                Log.d("collectionPurchasables: size: ${items?.size ?: 0}, items: ${
                    items?.sortedBy { it.billingSku.productId.productId }?.joinToString(separator = "\n") {
                        "  ${it.billingSku.productId.productId} : ${it.priceLocalized}"
                    }
                }")
            }
            .stateIn(coroutineScopeIo, SharingStarted.Eagerly, null)
    }

    override fun getCollectionPurchasable(collectionId: CollectionId): Flow<Purchasable.Collection?> =
        combine(
            entitlementSkuSpecs.getCollectionSkuSpec(collectionId),
            collectionPurchasables,
        ) { skuSpec, billingSkus ->
            billingSkus?.find { it.billingSku.productId == skuSpec?.productId }
        }

    override val allPurchasables: StateFlow<List<Purchasable>?> = combine(
        plusPurchasables,
        collectionPurchasables
    ) { plusSkus, collectionBillingSkus ->
        listOfNotNull(plusSkus, collectionBillingSkus)
            .flatten()
            .sortedBy { it.billingSkuSpec.productId.productId }
            .ifEmpty { null }
    }.onEach { skus ->
        Log.d("allBillingSkus: size: ${skus?.size ?: 0}, ids: ${skus?.joinToString { it.billingSkuSpec.productId.productId }}}")
    }.stateIn(coroutineScopeIo, SharingStarted.Eagerly, null)
}