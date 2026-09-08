package wallapp.content.network.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class NetworkContent(
    val wallpapers: List<NetworkWallpaper>,
    val categories: List<NetworkCategory>,
    val artists: List<NetworkArtist>,
    val folders: List<NetworkFolder>,
) {

    val exportString: String
        get() = json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

        fun fromExportString(exportString: String): NetworkContent {
            return json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}