package wallapp.image.host

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.image.ImageAction
import wallapp.image.OnImageAction
import wallapp.log.Logger
import wallapp.pixel.image.host.ImageHostFormatFallbackMapping

class ImageHostManagerDefault(
    private val imageHostManagerConfig: ImageHostManagerConfig,
    private val imageHostPlatformConfig: ImageHostPlatformConfig,
    coroutineScopeMain: CoroutineScope,
) : ImageHostManager {

    companion object {
        val Log = Logger("ImageHostManager")
    }

    private val galleryImageHostFormat: MutableStateFlow<ImageHostFormat> =
        MutableStateFlow(imageHostPlatformConfig.galleryImageHostFormat)

    private val imageFormatCodeInAppCompose: MutableStateFlow<String>
        get() = imageHostManagerConfig.imageFormatCodeInAppCompose

    private val inAppComposeImageHostFormat: StateFlow<ImageHostFormat> =
        imageFormatCodeInAppCompose.map { formatCode ->
            if (formatCode.isEmpty()) {
                imageHostPlatformConfig.inAppComposeImageHostFormat
            } else {
                ImageHostFormat.fromFormatCode(formatCode)
            }
        }.onEach {
            Log.i("inAppComposeImageHostFormat change: $it")
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = imageHostPlatformConfig.inAppComposeImageHostFormat,
        )

    private val inAppNativeImageHostFormat: MutableStateFlow<ImageHostFormat> =
        MutableStateFlow(imageHostPlatformConfig.inAppNativeImageHostFormat)

    override fun getImageHostFormat(imageHostDisplayTarget: ImageHostDisplayTarget): ImageHostFormat {
        return when (imageHostDisplayTarget) {
            ImageHostDisplayTarget.SystemGallery -> galleryImageHostFormat

            ImageHostDisplayTarget.InAppCompose -> inAppComposeImageHostFormat

            ImageHostDisplayTarget.InAppNative -> inAppNativeImageHostFormat
        }.value
    }

    override fun createOnImageAction(
        imageHostDisplayTarget: ImageHostDisplayTarget,
        dynamicUrlResult: ImageHostDynamicUrlResult,
    ): OnImageAction? {
        val imageHostFormat = dynamicUrlResult.imageHostFormat ?: return null
        return { imageAction: ImageAction ->
            if (imageAction is ImageAction.Error.ImageDecodeError) {
                onImageDecodeError(
                    imageHostDisplayTarget = imageHostDisplayTarget,
                    imageHostFormat = imageHostFormat,
                    imageUrl = imageAction.imageUrl,
                )
            }
        }
    }

    private fun onImageDecodeError(
        imageHostDisplayTarget: ImageHostDisplayTarget,
        imageHostFormat: ImageHostFormat,
        imageUrl: String,
    ) {
        when (imageHostDisplayTarget) {
            ImageHostDisplayTarget.InAppCompose -> onInAppComposeImageDecodeError(imageHostFormat, imageUrl)
            else -> throw IllegalArgumentException("Unsupported image host destination: $imageHostDisplayTarget")
        }
    }

    private fun onInAppComposeImageDecodeError(
        imageHostFormat: ImageHostFormat,
        imageUrl: String,
    ) {
        val fallbackFormat = inAppComposeFallbackMappings?.find { it.format == imageHostFormat }?.fallbackFormat
        if (imageUrl.endsWith("&fm=webp")) {
            Log.e("Image decode error for $imageHostFormat: $imageUrl")
        } else {
            Log.i("Image decode error for $imageHostFormat: $imageUrl")
        }
        if (fallbackFormat != null && inAppComposeImageHostFormat != fallbackFormat) {
            Log.w("Image decode error for $imageHostFormat, falling back to $fallbackFormat\n  $imageUrl")
            imageFormatCodeInAppCompose.value = fallbackFormat.formatCode
        }
    }

    private val inAppComposeFallbackMappings: List<ImageHostFormatFallbackMapping>?
        get() = imageHostPlatformConfig.inAppComposeFallbackMappings
}