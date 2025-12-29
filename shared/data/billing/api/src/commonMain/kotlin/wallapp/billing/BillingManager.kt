package wallapp.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.system.ui.controller.UiController

interface BillingManager {

    val connected: Flow<Boolean>

    val currentBillingPurchases: StateFlow<BillingPurchases?>

    suspend fun queryExpiredSubscriptions(): BillingSubscriptionsExpiredResult

    suspend fun queryPurchases(): BillingPurchasesQueryResult

    suspend fun queryBillingSkus(productIds: List<BillingProductId>): BillingSkusQueryResult

    fun initiatePurchase(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    )

    suspend fun initiatePurchaseSuspend(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean

    suspend fun restorePurchases()
}
