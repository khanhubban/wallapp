package wallapp.image.host

import wallapp.image.ImageSize

data class ImageHostDynamicUrlResult(
    val url: String,
    val imageSize: ImageSize?,
    val imageHostFormat: ImageHostFormat? = null,
)
