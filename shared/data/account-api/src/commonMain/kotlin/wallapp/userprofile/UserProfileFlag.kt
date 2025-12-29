package wallapp.userprofile

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
data class UserProfileFlag(
    val key: String,
    val value: String,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {

        fun fromExportString(exportString: String): UserProfileFlag? {
            if (exportString.isEmpty()) return null
            return try {
                Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
            } catch (e: SerializationException) {
                null
            }
        }

        fun fromExportStrings(exportStrings: List<String>?): List<UserProfileFlag>? {
            if (exportStrings.isNullOrEmpty()) {
                return null
            }
            return exportStrings
                .mapNotNull { fromExportString(it) }
                .ifEmpty { null }
        }
    }
}
