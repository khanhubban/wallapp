package wallapp.image.host

import wallapp.pixel.image.host.ImageHostFormatFallbackMapping


object ImageHostPlatformConfigAndroid : ImageHostPlatformConfig {

    /**
     * Avif on Android is too unreliable. See #851.
     */
    private val supportsAvif: Boolean
        get() = false
//        get() = Build.VERSION.SDK_INT >= 31

    override val galleryImageHostFormat: ImageHostFormat
        get() = ImageHostFormat.WebP

    override val inAppComposeImageHostFormat: ImageHostFormat
        get() {
            return if (supportsAvif) {
                ImageHostFormat.Avif
            } else {
                ImageHostFormat.WebP
            }
        }

    override val inAppNativeImageHostFormat: ImageHostFormat
        get() = inAppComposeImageHostFormat

    override val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
        get() = if (supportsAvif) {
            listOf(
                ImageHostFormatFallbackMapping(ImageHostFormat.Avif, ImageHostFormat.WebP),
//                ImageHostFormatFallbackMapping(ImageHostFormat.WebP, ImageHostFormat.Jpeg),
            )
        } else {
            null
        }
}