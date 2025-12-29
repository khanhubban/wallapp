package wallapp.purchase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id
import wallapp.data.purchase.CollectionBillingSkuSpec
import wallapp.data.purchase.Purchasable

object PurchasableRepositoryNoOp : PurchasableRepository {
    override val allPurchasables: StateFlow<List<Purchasable>?> = MutableStateFlow(null)
    override val plusPurchasables: StateFlow<List<Purchasable.Subscription>?> = MutableStateFlow(null)
    override val plusUnlimitedMonthlyPurchasable: StateFlow<Purchasable.SubscriptionUnlimitedMonthly?> = MutableStateFlow(null)
    override val plusUnlimitedAnnualPurchasable: StateFlow<Purchasable.SubscriptionUnlimitedAnnual?> = MutableStateFlow(null)
    override val plusStandardMonthlyPurchasable: StateFlow<Purchasable.SubscriptionStandardMonthly?> = MutableStateFlow(null)
    override val collectionPurchasables: StateFlow<List<Purchasable.Collection>?> = MutableStateFlow(null)

    override val plusPurchasableSkuSpecs: StateFlow<List<BillingSkuSpec>> = MutableStateFlow(emptyList())
    override val adFreePurchasableSkuSpecs: StateFlow<List<BillingSkuSpec>> = MutableStateFlow(emptyList())
    override val collectionPurchasableSkuSpecs: StateFlow<List<CollectionBillingSkuSpec>> = MutableStateFlow(emptyList())

    override fun getCollectionPurchasable(collectionId: Id.CollectionId): Flow<Purchasable.Collection?> = flowOf(null)
}