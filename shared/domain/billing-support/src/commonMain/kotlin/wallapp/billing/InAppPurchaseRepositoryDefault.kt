package wallapp.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.billing.purchase.BillingPurchases
import wallapp.billing.sku.BillingSkuSpec
import wallapp.coroutine.CoroutineScopeIo
import wallapp.coroutine.CoroutineScopeMain
import wallapp.log.Logger
import wallapp.system.ui.controller.UiControllerManager

class InAppPurchaseRepositoryDefault(
    private val billingManager: BillingManager,
    private val uiControllerManager: UiControllerManager,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
    @CoroutineScopeIo private val coroutineScopeIo: CoroutineScope,
): InAppPurchaseRepository() {

    companion object {
        val Log = Logger("[Billing] [InAppPurchaseRepositoryDefault]")
    }

    override val purchases = billingManager.currentBillingPurchases
        .onEach { Log.d("purchases: ${it?.toDebugString()}") }
        .map { it ?: BillingPurchases.Empty }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = billingManager.currentBillingPurchases.value
        )

    override fun initiatePurchase(billingSkuSpec: BillingSkuSpec, isSubscription: Boolean) {
        val uiController = uiControllerManager.currentUiController ?: throw IllegalStateException("No current activity")
        billingManager.initiatePurchase(uiController, billingSkuSpec, isSubscription)
    }

    override suspend fun initiatePurchaseSuspend(
        billingSkuSpec: BillingSkuSpec,
        isSubscription: Boolean
    ): Boolean {
        val uiController = uiControllerManager.currentUiController ?: throw IllegalStateException("No current activity")
        return billingManager.initiatePurchaseSuspend(uiController, billingSkuSpec, isSubscription)
    }

    override fun refreshPurchases() {
        Log.d("refreshPurchases()")
        coroutineScopeIo.launch {
            billingManager.queryPurchases()
        }
    }

    override fun restorePurchases() {
        Log.d("restorePurchases()")
        coroutineScopeIo.launch {
            billingManager.restorePurchases()
        }
    }
}
