package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.host.ImageHostPlatform

interface RemoteApiEndpointRepositoryConfig {

    val remoteEndpointTrack: StateFlow<RemoteEndpointTrack>

    val imageHostPlatform: StateFlow<ImageHostPlatform>

    val imageBucketSpec: Flow<ImageBucketSpec>
}