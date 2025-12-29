package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkLayerTranslation(
    val time: Int? = null,
    val angle: Int? = null,
    val direction: String? = null
)