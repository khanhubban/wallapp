package wallapp.media.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class NetworkMediaData(
    val version: Int,
    @SerialName("data")
    val mediaMap: Map<Long, NetworkMediaMap>,
) {

    val exportString: String
        get() = Json.encodeToString(serializer(), this)

    companion object {
        private val Json = Json { ignoreUnknownKeys = true }

        val Empty = NetworkMediaData(0, emptyMap())

        fun fromJson(jsonString: String): NetworkMediaData {
            return try {
                Json.decodeFromString<NetworkMediaData>(jsonString)
            } catch(ex: Exception) {
                Empty
            }
        }
    }
}