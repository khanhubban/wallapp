package wallapp.billing

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BillingResult(
    val success: Boolean,
    val responseCode: Int,
    val isUserFacingError: Boolean?,
    val debugMessage: String,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {

        fun fromExportString(exportString: String): BillingResult? {
            if (exportString.isEmpty()) return null

            if (exportString.isEmpty()) return null
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}
