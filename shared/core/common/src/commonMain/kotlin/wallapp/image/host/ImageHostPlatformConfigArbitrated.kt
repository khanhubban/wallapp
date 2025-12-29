package wallapp.image.host

import wallapp.pixel.image.host.ImageHostFormatFallbackMapping

/**
 * Used for Cron jobs
 */
class ImageHostPlatformConfigArbitrated(
    var imageHostPlatform: ImageHostPlatform? = null,
) : ImageHostPlatformConfig {

    private val current: ImageHostPlatformConfig
        get() = imageHostPlatform.let {
            requireNotNull(it) { "ImageHostPlatform must be set" }
            when (it) {
                ImageHostPlatform.Compose -> ImageHostPlatformConfigAndroid
                ImageHostPlatform.Apple -> ImageHostPlatformConfigIos
            }
        }

    override val galleryImageHostFormat: ImageHostFormat
        get() = current.galleryImageHostFormat
    override val inAppComposeImageHostFormat: ImageHostFormat
        get() = current.inAppComposeImageHostFormat
    override val inAppNativeImageHostFormat: ImageHostFormat
        get() = current.inAppNativeImageHostFormat
    override val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
        get() = current.inAppComposeFallbackMappings
}