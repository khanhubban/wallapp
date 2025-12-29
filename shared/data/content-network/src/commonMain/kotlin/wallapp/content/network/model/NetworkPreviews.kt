package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkPreviews(
    val standard: List<NetworkMedia>,
)