package wallapp.billing

import wallapp.billing.purchase.BillingPurchasePlay
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

interface BillingFactory {

    fun createBillingClient(
        context: Context,
        updateListener: PurchasesUpdatedListener,
    ): BillingClient

    fun createBillingPurchase(purchase: Purchase): BillingPurchasePlay
}