package wallapp.pixel.image.host

import wallapp.image.host.ImageHostFormat

data class ImageHostFormatFallbackMapping(
    val format: ImageHostFormat,
    val fallbackFormat: ImageHostFormat,
)
