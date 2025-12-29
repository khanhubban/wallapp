package wallapp.image.scaler

import wallapp.image.bucket.ImageBucketManager
import wallapp.image.bucket.ImageBucketSpec

class ImageScalerDefault(
    private val imageBucketManager: ImageBucketManager,
) : ImageScaler() {

    private val currentImageBucketSpec: ImageBucketSpec
        get() = requireNotNull(imageBucketManager.currentImageBucketSpec.value)

    override val density: Float
        get() = currentImageBucketSpec.maxDensity

    override val widthScale: Float
        get() = imageBucketManager.currentImageBucketWidthScale.value
    override val heightScale: Float
        get() = imageBucketManager.currentImageBucketHeightScale.value
}