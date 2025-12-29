package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkLayerModifier(
    val blendMode: String? = null,
    val tintColor: Int? = null,
    val flatColored: Boolean? = null,
)