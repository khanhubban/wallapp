package wallapp.search.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkSearchArtistMetadata(
    val artistId: String,
    val names: List<NetworkSearchEntry>,
)