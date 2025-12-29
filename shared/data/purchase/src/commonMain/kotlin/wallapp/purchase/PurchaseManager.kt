package wallapp.purchase

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.CollectionId
import wallapp.data.purchase.Purchasable

interface PurchaseManager {

    fun initiatePurchase(purchasable: Purchasable, isSubscription: Boolean)

    suspend fun initiatePurchaseSuspend(purchasable: Purchasable, isSubscription: Boolean): Boolean

    val purchasablePlusUnlimitedMonthlyRaw: Flow<Purchasable.SubscriptionUnlimitedMonthly?>
    val purchasablePlusUnlimitedMonthlyUserVisible: Flow<Purchasable.SubscriptionUnlimitedMonthly?>
    val purchasablePlusUnlimitedAnnualRaw: Flow<Purchasable.SubscriptionUnlimitedAnnual?>
    val purchasablePlusUnlimitedAnnualUserVisible: Flow<Purchasable.SubscriptionUnlimitedAnnual?>

    val purchasablePlusStandardMonthlyRaw: Flow<Purchasable.SubscriptionStandardMonthly?>
    val purchasablePlusStandardMonthlyUserVisible: Flow<Purchasable.SubscriptionStandardMonthly?>

    fun getPurchasableCollection(collectionId: CollectionId): Flow<Purchasable?>
}