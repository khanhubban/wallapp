package wallapp.content.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMedia(
    /**
     * If [type] is not specified, assume it is an image.
     */
    val type: String? = null,
    val id: Long,
    @SerialName("w") val width: Int? = null,
    @SerialName("h") val height: Int? = null,
    val blurHash: String?,
)