package wallapp.remoteendpoint

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.bucket.ImageBucketSpecs
import wallapp.image.host.ImageHostPlatform
import wallapp.image.host.getSystemImageHostPlatform

class RemoteApiEndpointRepositoryConfigPreset(
    override val remoteEndpointTrack: MutableStateFlow<RemoteEndpointTrack> =
        MutableStateFlow(RemoteEndpointTrack.Production),
    override val imageHostPlatform: MutableStateFlow<ImageHostPlatform> =
        MutableStateFlow(getSystemImageHostPlatform()),
    override val imageBucketSpec: MutableStateFlow<ImageBucketSpec> =
        MutableStateFlow(ImageBucketSpecs.Preset)
) : RemoteApiEndpointRepositoryConfig {

    constructor(track: RemoteEndpointTrack) : this(MutableStateFlow(track))
}
