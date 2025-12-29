package wallapp.search.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkSearchFolderMetadata(
    val folderId: String,
    val names: List<NetworkSearchEntry>,
)