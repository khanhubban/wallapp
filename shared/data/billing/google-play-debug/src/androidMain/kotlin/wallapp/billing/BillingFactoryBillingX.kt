package wallapp.billing

import wallapp.billing.purchase.BillingPurchasePlayBillingClientDebug
import wallapp.coroutine.CoroutineContexts
import wallapp.coroutine.collectIn
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.pixite.android.billingx.BillingStore
import com.pixite.android.billingx.DebugBillingClient
import kotlinx.coroutines.CoroutineScope

/**
 * Uses the BillingX library to mock purchases for debug builds.
 */
class BillingFactoryBillingX(
    context: Context,
    private val debugBillingDefinitions: DebugBillingDefinitions,
    private val coroutineContexts: CoroutineContexts,
    private val coroutineScopeMain: CoroutineScope,
): BillingFactory {

    private val billingStore: BillingStore by lazy { BillingStore.defaultStore(context) }

    override fun createBillingClient(
        context: Context,
        updateListener: PurchasesUpdatedListener,
    ): BillingClient {
        return DebugBillingClient(context,
            coroutineContexts,
            updateListener,
            this,
        )
    }

    override fun createBillingPurchase(
        purchase: Purchase,
    ) = BillingPurchasePlayBillingClientDebug(purchase, billingStore)

    init {
        debugBillingDefinitions.productDetails.collectIn(coroutineScopeMain) { productDetails ->
            billingStore.clearPurchases()
            val skus = productDetails.map { it.productId }.distinct()
            require(skus.size == productDetails.size) { "Each list item must use a unique SKU id" }
            productDetails.forEach {
                billingStore.addProduct(it)
            }
        }
    }
}