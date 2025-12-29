package wallapp.search.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkSearchRemixMetadata(
    val remixId: String,
    val artistNames: List<String>,
    val title: String,
    val collectionTitle: String?,
    val styles: List<NetworkSearchEntry>,
    val tags: List<NetworkSearchEntry>,
    val colors: List<NetworkSearchEntry>,
    val searchTerms: List<NetworkSearchEntry>,
    val titleSuggestions: List<NetworkSearchEntry>,
    val description: NetworkSearchEntry?,
)