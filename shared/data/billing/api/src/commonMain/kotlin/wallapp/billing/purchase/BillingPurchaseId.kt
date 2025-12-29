package wallapp.billing.purchase

import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * A unique identifier for a single purchase.
 * With Google Play, this is the orderId.
 * With RevenueCat, orderId is not exposed, so use a combination of data.
 */
@SealedInterop.Enabled
@Serializable
sealed class BillingPurchaseId {

    abstract val exportString: String

    @Serializable
    data class BillingPurchaseIdPlay(
        val orderId: String,
    ) : BillingPurchaseId() {

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Serializable
    data class BillingPurchaseIdRevenueCat(
        val productIdentifier: String,
        val productPlanIdentifier: String?,
        val originalPurchaseDateEpoch: Long,
    ) : BillingPurchaseId() {

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Serializable
    data class BillingPurchaseIdDebug(
        val id: String,
    ) : BillingPurchaseId() {

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BillingPurchaseId) return false

        if (this is BillingPurchaseIdPlay && other is BillingPurchaseIdPlay) {
            return orderId == other.orderId
        }
        if (this is BillingPurchaseIdDebug && other is BillingPurchaseIdDebug) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    companion object {

        fun fromExportString(exportString: String): BillingPurchaseId {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}


