package wallapp.billing.purchase

import com.android.billingclient.api.Purchase
import com.pixite.android.billingx.BillingStore


class BillingPurchasePlayBillingClientDebug(
    purchase: Purchase,
    private val billingStore: BillingStore,
) : BillingPurchasePlayBillingClient(purchase) {

    private val sku: String by lazy {
        purchase.skus.first()
    }

    private fun onUpdateValue() {
        billingStore.removePurchase(sku)
        billingStore.addPurchase(asPurchase())
    }

    override var isAcknowledged: Boolean = purchase.isAcknowledged
        set(value) {
            field = value
            onUpdateValue()
        }

    override var purchaseState: Int = purchase.purchaseState
        set(value) {
            field = value
            onUpdateValue()
        }
}
