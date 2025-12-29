package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkLayerRotation(
    val time: Int? = null,
    val direction: String? = null
)