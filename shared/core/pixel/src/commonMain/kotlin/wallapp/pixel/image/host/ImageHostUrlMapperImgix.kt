package wallapp.pixel.image.host

import wallapp.image.host.ImageHostDynamicUrlResult
import wallapp.image.host.ImageHostOptions
import wallapp.image.host.ImageHostUrlMapper
import wallapp.image.host.ImageHostUrlMapperConfig
import wallapp.image.host.ImageHostUrlMapperConfigMock
import wallapp.log.Logger
import wallapp.pixel.image.host.ImageHostExt.arbitrateImageSize
import wallapp.pixel.image.host.ImageHostImgixArbitrator.appendImgixArguments
import wallapp.unit.Size


class ImageHostUrlMapperImgix(
    private val config: ImageHostUrlMapperConfig,
) : ImageHostUrlMapper {

    companion object {
        val Log = Logger("ImageHostManager")

        const val TestImageGridSquare = "https://<appname>.imgix.net/test/testimage_grid_3200.png"
        var ForceUseTestImage = false

        private val String.isAlreadyFormatted: Boolean
            get() = this.contains("w=") || this.contains("h=")

        private val ImgixWidthArgumentRegex by lazy { "[?&]w=(\\d+)".toRegex() }
        private val ImgixHeightArgumentRegex by lazy { "[?&]h=(\\d+)".toRegex() }

        fun extractWidthAndHeight(url: String): Pair<Int?, Int?>? {
            val width = ImgixWidthArgumentRegex.find(url)?.groups?.get(1)?.value?.toIntOrNull()
            val height = ImgixHeightArgumentRegex.find(url)?.groups?.get(1)?.value?.toIntOrNull()

            return if (width == null && height == null) {
                null
            } else {
                Pair(width, height)
            }
        }
    }

    private val String.isImgixUrl: Boolean
        get() = config.hostPrefixes.any { this.startsWith(it) }

    private val String.isExcludedUrl: Boolean
        get() = config.exclusionUrlsComponents.any {
            this.contains(it)
        }

    override fun canMapDynamicImageUrl(imageUrl: String): Boolean {
        if (imageUrl.isImgixUrl) {
            if (imageUrl.isAlreadyFormatted) {
                Log.v("Image URL already formatted: $imageUrl")
                return false
            }
            if (imageUrl.isExcludedUrl) {
                Log.v("Image URL excluded: $imageUrl")
                return false
            }
            return true
        }

        return false
    }

    override fun mapDynamicImageUrl(
        imageUrl: String,
        options: ImageHostOptions,
        alignment: Any?
    ): ImageHostDynamicUrlResult {
        val url = if (ForceUseTestImage) { TestImageGridSquare } else { imageUrl }

        if (imageUrl.isImgixUrl) {
            val imageHostFormat = requireNotNull(options.imageHostFormat) { "ImageHostFormat is required" }
            val imageSize = arbitrateImageSize(options, alignment)

            /**
             * "example.png" is in `SizedImageGeneratorDefault.builderUrlModel`.
             */
//            val applyDebugBlend = !imageUrl.contains("example.png")
            val applyDebugBlend = false

            return appendImgixArguments(
                imageUrl = url,
                width = imageSize?.width,
                height = imageSize?.height,
                imageHostFormatCode = imageHostFormat.formatCode,
                crop = options.crop,
                applyDebugBlend = applyDebugBlend,
            ).let { result ->
                ImageHostDynamicUrlResult(
                    url = result,
                    imageSize = imageSize,
                    imageHostFormat = imageHostFormat,
                )
            }
        }

        return ImageHostDynamicUrlResult(imageUrl, imageSize = null)
    }

    private fun unmapImageSize(imageUrl: String): Size? {
        if (imageUrl.isImgixUrl) {
            return extractWidthAndHeight(imageUrl)
                ?.let { (width, height) ->
                    if (width == null || height == null) {
                        return null
                    } else {
                        Size(width = width, height = height)
                    }
                }
        }
        return null
    }
}

fun ImageHostUrlMapperImgixMock(
    config: ImageHostUrlMapperConfig = ImageHostUrlMapperConfigMock(),
): ImageHostUrlMapperImgix {
    return ImageHostUrlMapperImgix(
        config = config,
    )
}