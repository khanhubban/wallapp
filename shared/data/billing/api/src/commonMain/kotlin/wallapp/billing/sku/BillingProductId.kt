package wallapp.billing.sku

import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
@SealedInterop.Enabled
sealed class BillingProductId {

    abstract val productId: String
    abstract val entitlementId: String?

    abstract val exportString: String

    @Serializable
    data class BillingProductIdentifier(
        override val productId: String,
        override val entitlementId: String?,
    ) : BillingProductId() {

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

        // Interesting note: data class doesn't respect parent sealed class' equals and hashCode by default
        // It uses the default data class equals and hashCode implementation
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    @Serializable
    data class BillingProductIdWithPlan(
        override val productId: String,
        override val entitlementId: String?,
        val planId: String?,
    ) : BillingProductId() {

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BillingProductId) return false

        if (this is BillingProductIdentifier && other is BillingProductIdentifier) {
            return productId == other.productId
        }
        if (this is BillingProductIdWithPlan && other is BillingProductIdWithPlan) {
            return productId == other.productId && planId == other.planId
        }
        return false
    }

    override fun hashCode(): Int {
        return when (this) {
            is BillingProductIdentifier -> productId.hashCode()
            is BillingProductIdWithPlan -> 31 * productId.hashCode() + (planId?.hashCode() ?: 0)
        }
    }

    companion object {
        fun from(productId: String, entitlementId: String? = null, planId: String? = null): BillingProductId {
            return if (planId == null) {
                BillingProductIdentifier(productId, entitlementId)
            } else {
                BillingProductIdWithPlan(productId, entitlementId, planId)
            }
        }

        fun fromExportString(exportString: String): BillingProductId {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}
