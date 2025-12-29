package wallapp.purchase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.CollectionId
import wallapp.data.purchase.Purchasable

class PurchaseManagerNoOp : PurchaseManager {
    override fun initiatePurchase(purchasable: Purchasable, isSubscription: Boolean) = Unit

    override suspend fun initiatePurchaseSuspend(purchasable: Purchasable, isSubscription: Boolean): Boolean = false

    override val purchasablePlusUnlimitedMonthlyRaw: Flow<Purchasable.SubscriptionUnlimitedMonthly?> =
        flowOf(null)
    override val purchasablePlusUnlimitedMonthlyUserVisible: Flow<Purchasable.SubscriptionUnlimitedMonthly?> =
        flowOf(null)

    override val purchasablePlusUnlimitedAnnualRaw: Flow<Purchasable.SubscriptionUnlimitedAnnual?> =
        flowOf(null)
    override val purchasablePlusUnlimitedAnnualUserVisible: Flow<Purchasable.SubscriptionUnlimitedAnnual?> =
        flowOf(null)

    override val purchasablePlusStandardMonthlyRaw: Flow<Purchasable.SubscriptionStandardMonthly?> =
        flowOf(null)
    override val purchasablePlusStandardMonthlyUserVisible: Flow<Purchasable.SubscriptionStandardMonthly?> =
        flowOf(null)

    override fun getPurchasableCollection(collectionId: CollectionId): Flow<Purchasable?> = flowOf(null)
}