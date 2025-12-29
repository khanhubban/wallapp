package wallapp.image.bucket

import kotlinx.serialization.Serializable

@Serializable
data class ImageBucketSpec(
    val key: String,
    val label: String,
    val maxWidthPx: Int,
    val maxHeightPx: Int,
    val maxDensity: Float,
)
