package wallapp.content.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkWallpaper(
    val id: String,
    val label: String,
    val collectionLabel: String,
    val type: String,
    val artistId: String,
    @SerialName("dlm") val wallpaperDownloadMedia: NetworkWallpaperDownloadMedia,
    val isDark: Boolean,
    val topColorShade: String? = null,
    val categoryId: String,
    val isSingle: Boolean,
    val previews: NetworkPreviews,
    val slugs: List<String>,
    @SerialName("aie") val isAiEnhanced: Boolean = false,
    @SerialName("free") val isFree: Boolean = false,
)