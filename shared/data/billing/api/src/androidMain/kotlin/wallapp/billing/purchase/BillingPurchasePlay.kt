package wallapp.billing.purchase

import wallapp.billing.account.AccountIdentifiers
import wallapp.billing.purchase.BillingPurchaseId.BillingPurchaseIdPlay

/**
 * Represents a purchase made through Google Play.
 */
abstract class BillingPurchasePlay : BillingPurchase {
    override val id: BillingPurchaseId?
        get() = orderId?.let { BillingPurchaseIdPlay(it) }
    abstract val orderId: String?
    abstract val accountIdentifiers: AccountIdentifiers?
    abstract val developerPayload: String
    abstract val originalJson: String
    abstract val packageName: String
    abstract val purchaseState: Int     // See BILLING_PURCHASE_STATE_*
    override val isPurchasePending: Boolean
        get() = purchaseState == BILLING_PURCHASE_STATE_PENDING
    val isPurchased: Boolean
        get() = purchaseState == BILLING_PURCHASE_STATE_PURCHASED
    abstract val purchaseTime: Long
    abstract val purchaseToken: String
    abstract val quantity: Int
    abstract val signature: String

    abstract val isAcknowledged: Boolean
    abstract val isAutoRenewing: Boolean

    /**
     * [true] if the purchase has been registered as consumed or acknowledged with the billing provider.
     */
    abstract val isFinalized: Boolean

    override fun hashCode(): Int {
        var result: Int = id.hashCode()
        accountIdentifiers?.also { result = 31 * result + it.hashCode() }
        result = 31 * result + developerPayload.hashCode()
        result = 31 * result + originalJson.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + purchaseState.hashCode()
        result = 31 * result + purchaseTime.hashCode()
        result = 31 * result + purchaseToken.hashCode()
        result = 31 * result + quantity.hashCode()
        result = 31 * result + signature.hashCode()
        result = 31 * result + skuSpecs.hashCode()
        result = 31 * result + isAcknowledged.hashCode()
        result = 31 * result + isAutoRenewing.hashCode()
        result = 31 * result + isFinalized.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if(other !is BillingPurchasePlay) return false

        val thisAccountIdentifiers = accountIdentifiers
        val thisDeveloperPayload = developerPayload
        val thisOrderId = orderId
        val thisOriginalJson = originalJson
        val thisPackageName = packageName
        val thisPurchaseState = purchaseState
        val thisPurchaseTime = purchaseTime
        val thisPurchaseToken = purchaseToken
        val thisQuantity = quantity
        val thisSignature = signature
        val thisProductDescriptors = skuSpecs
        val thisIsAcknowledged = isAcknowledged
        val thisIsAutoRenewing = isAutoRenewing
        val thisIsFinalized = isFinalized

        val otherAccountIdentifiers = other.accountIdentifiers
        val otherDeveloperPayload = other.developerPayload
        val otherOrderId = other.orderId
        val otherOriginalJson = other.originalJson
        val otherPackageName = other.packageName
        val otherPurchaseState = other.purchaseState
        val otherPurchaseTime = other.purchaseTime
        val otherPurchaseToken = other.purchaseToken
        val otherQuantity = other.quantity
        val otherSignature = other.signature
        val otherProductDescriptors = other.skuSpecs
        val otherIsAcknowledged = other.isAcknowledged
        val otherIsAutoRenewing = other.isAutoRenewing
        val otherIsFinalized = other.isFinalized
        
        return thisAccountIdentifiers == otherAccountIdentifiers
                && thisDeveloperPayload == otherDeveloperPayload
                && thisOrderId == otherOrderId
                && thisOriginalJson == otherOriginalJson
                && thisPackageName == otherPackageName
                && thisPurchaseState == otherPurchaseState
                && thisPurchaseTime == otherPurchaseTime
                && thisPurchaseToken == otherPurchaseToken
                && thisQuantity == otherQuantity
                && thisSignature == otherSignature
                && thisProductDescriptors == otherProductDescriptors
                && thisIsAcknowledged == otherIsAcknowledged
                && thisIsAutoRenewing == otherIsAutoRenewing
                && thisIsFinalized == otherIsFinalized
    }

    override fun toString(): String = debugString

    override val debugString: String
        get() = "BillingPurchase(productIds: [${skuSpecs.map { it.productId }}], " +
                "purchaseState: ${getPurchaseStateDescription(purchaseState)}, " +
                "isFinalized: $isFinalized, isAcknowledged: $isAcknowledged, " +
                "orderId: $orderId, " +
                "purchaseToken: $purchaseToken, " +
                "purchaseTime: $purchaseTime, " +
                "packageName: $packageName, " +
                "quantity: $quantity, " +
                "isAutoRenewing: $isAutoRenewing, " +
                "signature: $signature, " +
                "originalJson: $originalJson)"

    override val debugStringShort: String
        get() = "BillingPurchase(productIds: [${skuSpecs.map { it.productId }}])"

}

