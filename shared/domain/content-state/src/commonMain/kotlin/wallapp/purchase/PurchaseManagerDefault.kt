package wallapp.purchase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wallapp.billing.InAppPurchaseRepository
import wallapp.content.model.Id.CollectionId
import wallapp.data.purchase.Purchasable
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.util.combine

class PurchaseManagerDefault(
    private val inAppPurchaseRepository: InAppPurchaseRepository,
    private val purchasableRepository: PurchasableRepository,
    private val remoteConfigData: RemoteConfigData,
) : PurchaseManager {

    override fun initiatePurchase(purchasable: Purchasable, isSubscription: Boolean) {
        inAppPurchaseRepository.initiatePurchase(purchasable.billingSkuSpec, isSubscription)
    }

    override suspend fun initiatePurchaseSuspend(
        purchasable: Purchasable,
        isSubscription: Boolean
    ): Boolean =
        inAppPurchaseRepository.initiatePurchaseSuspend(purchasable.billingSkuSpec, isSubscription)

    private val annualSubscriptionEnabled: Flow<Boolean>
        get() = remoteConfigData.upgradeEnableAnnualSubscription
    private val anySubscriptionsEnabled: Flow<Boolean>
        get() = remoteConfigData.upgradeEnableAnySubscriptions

    override val purchasablePlusUnlimitedMonthlyRaw: Flow<Purchasable.SubscriptionUnlimitedMonthly?>
        get() = purchasableRepository.plusUnlimitedMonthlyPurchasable
    override val purchasablePlusUnlimitedMonthlyUserVisible: Flow<Purchasable.SubscriptionUnlimitedMonthly?> by lazy {
        combine(
            anySubscriptionsEnabled,
            purchasablePlusUnlimitedMonthlyRaw,
        ) { anySubscriptionsEnabled, purchasable ->
            if (anySubscriptionsEnabled) {
                purchasable
            } else {
                null
            }
        }
    }
    override val purchasablePlusUnlimitedAnnualRaw: Flow<Purchasable.SubscriptionUnlimitedAnnual?>
        get() = purchasableRepository.plusUnlimitedAnnualPurchasable
    override val purchasablePlusUnlimitedAnnualUserVisible: Flow<Purchasable.SubscriptionUnlimitedAnnual?> by lazy {
        combine(
            annualSubscriptionEnabled,
            purchasablePlusUnlimitedAnnualRaw,
        ) { annualSubscriptionEnabled, purchasable ->
            if (annualSubscriptionEnabled) {
                purchasable
            } else {
                null
            }
        }
    }

    override val purchasablePlusStandardMonthlyRaw: Flow<Purchasable.SubscriptionStandardMonthly?>
        get() = purchasableRepository.plusStandardMonthlyPurchasable
    override val purchasablePlusStandardMonthlyUserVisible: Flow<Purchasable.SubscriptionStandardMonthly?> by lazy {
        combine(
            anySubscriptionsEnabled,
            purchasablePlusStandardMonthlyRaw,
        ) { anySubscriptionsEnabled, purchasable ->
            if (anySubscriptionsEnabled) {
                purchasable
            } else {
                null
            }
        }
    }

    override fun getPurchasableCollection(collectionId: CollectionId): Flow<Purchasable?> =
        purchasableRepository.getCollectionPurchasable(collectionId)
            .map { purchasable ->
                if (purchasable == null) return@map null
                Purchasable.Collection(
                    collectionId = collectionId,
                    billingSku = purchasable.billingSku,
                )
            }
}