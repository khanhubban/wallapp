package wallapp.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingSkuSpec


abstract class InAppPurchaseRepository {

    abstract val purchases: StateFlow<BillingPurchases?>

    /**
     * Monitor [purchases] for any changes.
     */
    abstract fun initiatePurchase(billingSkuSpec: BillingSkuSpec, isSubscription: Boolean)

    abstract suspend fun initiatePurchaseSuspend(billingSkuSpec: BillingSkuSpec, isSubscription: Boolean): Boolean

    abstract fun refreshPurchases()

    abstract fun restorePurchases()

}


class InAppPurchaseRepositoryNoOp: InAppPurchaseRepository() {

    override val purchases: MutableStateFlow<BillingPurchases?> = MutableStateFlow(null)

    override fun initiatePurchase(billingSkuSpec: BillingSkuSpec, isSubscription: Boolean) { }

    override suspend fun initiatePurchaseSuspend(billingSkuSpec: BillingSkuSpec, isSubscription: Boolean): Boolean = false

    override fun refreshPurchases() { }

    override fun restorePurchases() { }
}
