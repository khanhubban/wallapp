package wallapp.image.host

import wallapp.pixel.image.host.ImageHostFormatFallbackMapping

object ImageHostPlatformConfigPreset : ImageHostPlatformConfig {

    override val galleryImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.WebP
    override val inAppComposeImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.WebP
    override val inAppNativeImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.WebP
    override val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
        get() = null
}