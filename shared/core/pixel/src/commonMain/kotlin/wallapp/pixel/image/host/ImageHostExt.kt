package wallapp.pixel.image.host

import wallapp.image.ImageSize
import wallapp.image.host.ImageHostFormat
import wallapp.image.host.ImageHostOptions
import wallapp.image.host.ImageHostOptionsDefault
import wallapp.image.scaler.ImageScaler
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.host.ImageHostImgixArbitrator.arbitrateAlignmentSize

/**
 * Helper functions to ensure the same logic is used across the app. Typically these functions
 * would be in a single class, but image mapping is complex. Specifically, we need to ensure the
 * image size is arbitrated correctly, both during the offline image mapping process, and at
 * runtime, when the image mapping code is not available.
 */
object ImageHostExt {

    fun ImageHostOptionsScaler(
        imageScaler: ImageScaler,
        imageHostFormat: ImageHostFormat,
        imageViewSpec: ImageViewSpec,
        applyCrop: Boolean,
    ): ImageHostOptions {
        return ImageHostOptionsDefault(imageHostFormat) {
            imageScaler.apply(this, imageViewSpec.width.value, imageViewSpec.height.value)
            crop = applyCrop
        }
    }

    fun arbitrateImageSize(
        imageHostOptions: ImageHostOptions,
        alignment: Any?,
    ): ImageSize? {
        val (adjustedWidth, adjustedHeight) =
            arbitrateAlignmentSize(alignment, imageHostOptions.width, imageHostOptions.height)
        return if (adjustedWidth != null && adjustedHeight != null) {
            ImageSize(width = adjustedWidth, height = adjustedHeight)
        } else {
            null
        }
    }

}

