package wallapp.ads

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class AdEvent(
    val time: Long,
    val levelIncrease: Double,
) {

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): AdEvent? {
            if (exportString.isEmpty()) return null
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }

}
