package wallapp.billing

import wallapp.billing.purchase.BillingPurchasePlay
import wallapp.billing.purchase.BillingPurchasePlayBillingClient
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

/**
 * Uses PlayStore billing library.
 */
class BillingFactoryPlay : BillingFactory {

    override fun createBillingClient(
        context: Context,
        updateListener: PurchasesUpdatedListener,
    ): BillingClient {
        return BillingClient
            .newBuilder(context)
            .enablePendingPurchases()
            .setListener(updateListener)
            .build()
    }

    override fun createBillingPurchase(purchase: Purchase): BillingPurchasePlay =
        BillingPurchasePlayBillingClient(purchase)
}