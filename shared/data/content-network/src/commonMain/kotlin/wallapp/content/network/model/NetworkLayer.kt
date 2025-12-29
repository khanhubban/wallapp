package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkLayer(
    val type: String = "",
    val rotation: NetworkLayerRotation? = null,
    val translation: NetworkLayerTranslation? = null,
    val parallaxScale: Float = 1f,
    val parallaxOnlyOnTilt: Boolean = false,
    val imageSubsetLayoutParams: NetworkImageSubLayoutParams? = null,
    val centerInCutout: Boolean = false,
    // If > -1, apply the same position offset as is applied to the item at the specified index.
    // Allows one item to be positioned in the cutout, and other items that must be positioned
    // relative to that cutout item to have their positions adjusted also.
    val inheritLayerIndexCutoutOffset: Int = -1,
    // If true, the image will be scaled up slightly to work with system zoom.
    // If false, the image will be positioned exactly as per [imageSubsetLayoutParams].
    val allowZoomScale: Boolean = true,
    val media: List<NetworkMedia>? = null,
    val modifier: NetworkLayerModifier? = null,
)