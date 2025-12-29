package wallapp.image.size

import wallapp.image.ImageSize
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.sized.SizedImage

interface ImageSizeMapper {

    fun getImageSize(
        sizedImage: SizedImage,
        imageBucketSpec: ImageBucketSpec,
    ): ImageSize?

}