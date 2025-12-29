package wallapp.remoteendpoint

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RemoteEndpoints(
    val content: String,
    val search: String,
    val media: RemoteEndpointMediaMap,
) {

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun from(string: String): RemoteEndpoints {
            return Json.decodeFromString(kotlinx.serialization.serializer(), string)
        }
    }
}
