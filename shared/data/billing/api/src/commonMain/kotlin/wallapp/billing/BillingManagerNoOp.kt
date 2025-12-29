package wallapp.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingSkuSpec
import wallapp.system.ui.controller.UiController


class BillingManagerNoOp : BillingManager {

    override val connected: StateFlow<Boolean> = MutableStateFlow(true)

    override val currentBillingPurchases: StateFlow<BillingPurchases?> = MutableStateFlow(null)

    override suspend fun queryExpiredSubscriptions(): BillingSubscriptionsExpiredResult {
        return BillingSubscriptionsExpiredResult.None
    }

    override suspend fun queryPurchases(): BillingPurchasesQueryResult {
        return BillingPurchasesQueryResult.Error("BillingManagerNoOp.queryPurchases() not implemented")
    }

    override suspend fun queryBillingSkus(productIds: List<BillingProductId>): BillingSkusQueryResult {
        return BillingSkusQueryResult.Error("BillingManagerNoOp.queryBillingSkus() not implemented")
    }

    override fun initiatePurchase(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ) { }

    override suspend fun initiatePurchaseSuspend(
        uiController: UiController,
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean {
        return false
    }

    override suspend fun restorePurchases() { }
}