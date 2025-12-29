package wallapp.image.host

import wallapp.pixel.image.host.ImageHostFormatFallbackMapping


object ImageHostPlatformConfigDesktop : ImageHostPlatformConfig {

    override val galleryImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.Png

    override val inAppComposeImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.WebP

    override val inAppNativeImageHostFormat: ImageHostFormat
        get() = inAppComposeImageHostFormat

    override val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
        get() = null
}