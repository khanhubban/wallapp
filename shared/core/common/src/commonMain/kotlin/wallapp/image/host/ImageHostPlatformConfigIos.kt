package wallapp.image.host

import wallapp.pixel.image.host.ImageHostFormatFallbackMapping


object ImageHostPlatformConfigIos : ImageHostPlatformConfig {

    override val galleryImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.Jpg

    override val inAppComposeImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.WebP

    override val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
        get() = null

    /**
     * iOS supports Avif when rendering via native UIKit
     */
    override val inAppNativeImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.Avif
}