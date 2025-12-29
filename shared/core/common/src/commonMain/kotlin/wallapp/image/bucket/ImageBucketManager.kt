package wallapp.image.bucket

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ImageBucketManager {

    val currentImageBucketSpec: StateFlow<ImageBucketSpec?>

    /**
     * The scale factor to apply to the width/height of an image to match the size of the image in
     * the current image bucket. Applying this value does mean that images will either be exact size
     * matches, or a bit larger than the ideal visible size for the device.
     * This trade-off is accepted so:
     *   1. The app can group items into prebuilt buckets
     *   2. The image host has (much) more of a chance of being able to serve the image from cache
     *   rather than needing to rebuild/resize it.
     *   3. (Bonus): When we consider the app can be used in resizable windows, use of buckets is
     *   unavoidable.
     */
    val currentImageBucketWidthScale: StateFlow<Float>
    val currentImageBucketHeightScale: StateFlow<Float>
}

object ImageBucketManagerNoOp : ImageBucketManager {
    override val currentImageBucketSpec = MutableStateFlow(null)
    override val currentImageBucketWidthScale = MutableStateFlow(1f)
    override val currentImageBucketHeightScale = MutableStateFlow(1f)
}

class ImageBucketManagerMock(
    override val currentImageBucketSpec: StateFlow<ImageBucketSpec>,
    val deviceImageBucketSpec: ImageBucketSpec = currentImageBucketSpec.value,
) : ImageBucketManager {

    constructor(
        currentImageBucketSpec: ImageBucketSpec,
        deviceImageBucketSpec: ImageBucketSpec = currentImageBucketSpec,
    ) : this(MutableStateFlow(currentImageBucketSpec), deviceImageBucketSpec)

    override val currentImageBucketWidthScale by lazy {
        MutableStateFlow(currentImageBucketSpec.value.maxWidthPx.toFloat() / deviceImageBucketSpec.maxWidthPx)
    }
    override val currentImageBucketHeightScale: StateFlow<Float> by lazy {
        MutableStateFlow(currentImageBucketSpec.value.maxHeightPx.toFloat() / deviceImageBucketSpec.maxHeightPx)
    }
}