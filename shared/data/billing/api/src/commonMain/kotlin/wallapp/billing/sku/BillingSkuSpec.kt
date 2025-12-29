package wallapp.billing.sku

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BillingSkuSpec(
//    val productType: BillingProductType,
    val productId: BillingProductId,
) {

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): BillingSkuSpec {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}