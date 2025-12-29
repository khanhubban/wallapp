package wallapp.mediamap

import kotlinx.coroutines.flow.StateFlow
import wallapp.image.bucket.ImageBucketManager
import wallapp.image.bucket.ImageBucketSpec

class MediaMapRepositoryConfigDefault(
    private val imageBucketManager: ImageBucketManager,
) : MediaMapRepositoryConfig {

    override val imageBucketSpec: StateFlow<ImageBucketSpec?>
        get() = imageBucketManager.currentImageBucketSpec
}