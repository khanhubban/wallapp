package wallapp.image.sized

import kotlinx.serialization.Serializable
import wallapp.image.ImageSize

@Serializable
data class SizedImageResult(
    val urlSuffix: String,
    val imageBucketSpecKey: String,
    val sizedImage: SizedImage,
    val imageHostFormatKey: String,
    val imageSize: ImageSize?,
)
