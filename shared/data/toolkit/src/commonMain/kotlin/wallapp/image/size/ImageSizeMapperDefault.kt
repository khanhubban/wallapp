package wallapp.image.size

import wallapp.image.ImageSize
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.host.ImageHostFormat
import wallapp.image.scaler.ImageScaler
import wallapp.image.sized.SizedImage
import wallapp.image.sized.SizedImageMapper
import wallapp.pixel.image.host.ImageHostExt.ImageHostOptionsScaler
import wallapp.pixel.image.host.ImageHostExt.arbitrateImageSize

class ImageSizeMapperDefault(
    private val imageScaler: ImageScaler,
    private val sizedImageMapper: SizedImageMapper,
) : ImageSizeMapper {

    override fun getImageSize(
        sizedImage: SizedImage,
        imageBucketSpec: ImageBucketSpec
    ): ImageSize? {
        return when (sizedImage) {
            SizedImage.DownloadableWallpaperHd -> {
                null
            }
            SizedImage.DownloadableWallpaperSd -> {
                null
            }

            else -> {
                val imageViewSpec = sizedImageMapper.mapImageViewSpec(sizedImage)
                val options = ImageHostOptionsScaler(
                    imageScaler = imageScaler,
                    imageHostFormat = ImageHostFormat.Auto,
                    imageViewSpec = imageViewSpec,
                    applyCrop = sizedImage.applyCrop,
                )
                val imageSize = arbitrateImageSize(options, imageViewSpec.alignment)
                imageSize
            }
        }
    }
}