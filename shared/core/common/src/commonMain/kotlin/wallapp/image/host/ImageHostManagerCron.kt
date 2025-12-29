package wallapp.image.host

import wallapp.image.OnImageAction

class ImageHostManagerCron(
    private val imageHostPlatformConfig: ImageHostPlatformConfig,
) : ImageHostManager {
    override fun getImageHostFormat(imageHostDisplayTarget: ImageHostDisplayTarget): ImageHostFormat {
        return when (imageHostDisplayTarget) {
            ImageHostDisplayTarget.SystemGallery -> imageHostPlatformConfig.galleryImageHostFormat

            ImageHostDisplayTarget.InAppCompose -> imageHostPlatformConfig.inAppComposeImageHostFormat

            ImageHostDisplayTarget.InAppNative -> imageHostPlatformConfig.inAppNativeImageHostFormat
        }
    }

    override fun createOnImageAction(
        imageHostDisplayTarget: ImageHostDisplayTarget,
        dynamicUrlResult: ImageHostDynamicUrlResult
    ): OnImageAction? = null
}
