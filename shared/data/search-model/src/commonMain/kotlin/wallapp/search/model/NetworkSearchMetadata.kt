package wallapp.search.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class NetworkSearchMetadata(
    val remixMetadata: List<NetworkSearchRemixMetadata>,
    val artistMetadata: List<NetworkSearchArtistMetadata>,
    val folderMetadata: List<NetworkSearchFolderMetadata>,
//    val collectionMetadata: List<NetworkSearchCollectionMetadata>,
) {
    val exportString: String
        get() = json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        private val json = Json { ignoreUnknownKeys = false }

        fun fromExportString(exportString: String): NetworkSearchMetadata {
            return json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}
