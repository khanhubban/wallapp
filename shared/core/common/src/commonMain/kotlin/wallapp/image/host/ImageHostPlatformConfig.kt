package wallapp.image.host

import wallapp.pixel.image.host.ImageHostFormatFallbackMapping

interface ImageHostPlatformConfig {

    val galleryImageHostFormat: ImageHostFormat

    val inAppComposeImageHostFormat: ImageHostFormat
    val inAppNativeImageHostFormat: ImageHostFormat

    val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
}