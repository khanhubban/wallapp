package wallapp.purchase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.billing.purchase.BillingPurchaseId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.content.model.Id.CollectionId
import wallapp.data.purchase.CollectionBillingSkuSpec
import wallapp.data.purchase.Purchasable

interface PurchasableRepository {

    val allPurchasables: StateFlow<List<Purchasable>?>

    val plusPurchasables: StateFlow<List<Purchasable.Subscription>?>
    val plusUnlimitedMonthlyPurchasable: StateFlow<Purchasable.SubscriptionUnlimitedMonthly?>
    val plusUnlimitedAnnualPurchasable: StateFlow<Purchasable.SubscriptionUnlimitedAnnual?>
    val plusStandardMonthlyPurchasable: StateFlow<Purchasable.SubscriptionStandardMonthly?>

    val collectionPurchasables: StateFlow<List<Purchasable.Collection>?>

    val plusPurchasableSkuSpecs: StateFlow<List<BillingSkuSpec>>
    val adFreePurchasableSkuSpecs: StateFlow<List<BillingSkuSpec>>
    val collectionPurchasableSkuSpecs: StateFlow<List<CollectionBillingSkuSpec>>

    fun getCollectionPurchasable(collectionId: CollectionId): Flow<Purchasable.Collection?>
}

suspend fun PurchasableRepository.getByBillingPurchaseId(billingPurchaseId: BillingPurchaseId): Purchasable? {
    return allPurchasables.value?.find { purchasable ->
        purchasable.billingSkuSpec.productId == billingPurchaseId
    }
}
