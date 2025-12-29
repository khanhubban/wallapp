package wallapp.billing.purchase

import wallapp.billing.account.AccountIdentifiers
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingProductType
import wallapp.billing.sku.BillingSkuSpec
import com.android.billingclient.api.Purchase


open class BillingPurchasePlayBillingClient(
    private val purchase: Purchase,
    private val billingProductType: BillingProductType = BillingProductType.InApp,
) : BillingPurchasePlay() {
    override val accountIdentifiers: AccountIdentifiers?
        get() = purchase.accountIdentifiers
            ?.let {
                if (it.obfuscatedAccountId == null && it.obfuscatedProfileId == null) {
                    null
                } else {
                    AccountIdentifiers(it.obfuscatedAccountId, it.obfuscatedProfileId)
                }
            }

    override val developerPayload: String
        get() = purchase.developerPayload
    override val orderId: String?
        get() = purchase.orderId
    override val originalJson: String
        get() = purchase.originalJson
    override val packageName: String
        get() = purchase.packageName
    override val purchaseState: Int
        get() = purchase.purchaseState
    override val purchaseTime: Long
        get() = purchase.purchaseTime
    override val purchaseToken: String
        get() = purchase.purchaseToken
    override val quantity: Int
        get() = purchase.quantity
    override val signature: String
        get() = purchase.signature
    override val skuSpecs: List<BillingSkuSpec>
        get() = purchase.skus.map { BillingSkuSpec(BillingProductId.from(it)) }
    override val isAcknowledged: Boolean
        get() = purchase.isAcknowledged
    override val isAutoRenewing: Boolean
        get() = purchase.isAutoRenewing

    override val isFinalized: Boolean
        // TODO: Support consumable IAPs
        get() = purchaseState == Purchase.PurchaseState.PURCHASED && isAcknowledged
}
