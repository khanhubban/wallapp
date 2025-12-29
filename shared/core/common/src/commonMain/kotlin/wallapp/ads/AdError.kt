package wallapp.ads

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AdError(
    val code: AdErrorCode,
    val message: String,
    val domain: String,
) {

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): AdError? {
            if (exportString.isEmpty()) return null
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}
