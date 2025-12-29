package wallapp.billing.debug.purchase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.billing.purchase.BillingPurchase
import wallapp.billing.purchase.BillingPurchaseId
import wallapp.billing.sku.BillingSkuSpec

@Serializable
data class BillingPurchaseDebug(
    override val id: BillingPurchaseId?,
    override val isPurchasePending: Boolean,
    override val skuSpecs: List<BillingSkuSpec>,
) : BillingPurchase {

    constructor(
        orderId: BillingPurchaseId?,
        skuSpec: BillingSkuSpec,
    ) : this(orderId, isPurchasePending = false, listOf(skuSpec))

    override val debugStringShort: String
        get() = "id: $id, isPurchasePending: $isPurchasePending, skuSpecs: $skuSpecs"
    override val debugString: String
        get() = debugStringShort

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): BillingSkuSpec {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}

