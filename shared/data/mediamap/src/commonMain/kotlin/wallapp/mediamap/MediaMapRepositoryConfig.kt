package wallapp.mediamap

import kotlinx.coroutines.flow.StateFlow
import wallapp.image.bucket.ImageBucketSpec

interface MediaMapRepositoryConfig {

    val imageBucketSpec: StateFlow<ImageBucketSpec?>
}