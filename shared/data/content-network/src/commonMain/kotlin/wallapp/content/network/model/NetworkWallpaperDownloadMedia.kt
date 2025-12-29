package wallapp.content.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkWallpaperDownloadMedia(
    @SerialName("w") val hdWidth: Int,
    @SerialName("h") val hdHeight: Int,
    @SerialName("hd") val hdMediaId: Long,
    @SerialName("sd") val sdMediaId: Long,
)
