package wallapp.mediamap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.image.bucket.ImageBucketSpec

class MediaMapRepositoryConfigPreset(
    override val imageBucketSpec: StateFlow<ImageBucketSpec?>,
) : MediaMapRepositoryConfig {

    constructor(imageBucketSpec: ImageBucketSpec) : this(
        imageBucketSpec = MutableStateFlow(imageBucketSpec)
    )
}