package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import wallapp.image.bucket.ImageBucketManager
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.host.ImageHostPlatform
import wallapp.image.host.getSystemImageHostPlatform

class RemoteApiEndpointRepositoryConfigDefault(
    private val imageBucketManager: ImageBucketManager,
) : RemoteApiEndpointRepositoryConfig {

    override val remoteEndpointTrack: StateFlow<RemoteEndpointTrack> =
        MutableStateFlow(RemoteEndpointTrack.Production)
    override val imageHostPlatform: StateFlow<ImageHostPlatform> =
        MutableStateFlow(getSystemImageHostPlatform())

    private val currentImageBucketSpec: StateFlow<ImageBucketSpec?>
        get() = imageBucketManager.currentImageBucketSpec

    override val imageBucketSpec: Flow<ImageBucketSpec>
        get() = currentImageBucketSpec.filterNotNull()
}