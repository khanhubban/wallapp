package wallapp.remoteendpoint

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RemoteApiEndpointRepositoryPreset(
    val remoteEndpointsRepositoryPreset: RemoteEndpointsRepositoryPreset,
) : RemoteApiEndpointRepository {

    private val remoteEndpoints: RemoteEndpoints
        get() = remoteEndpointsRepositoryPreset.remoteEndpointsProduction

    override val contentApiUrl: Flow<String> by lazy {
        flowOf(remoteEndpoints.content)
    }

    override val searchApiUrl: Flow<String> by lazy {
        flowOf(remoteEndpoints.search)
    }

    override val mediaMapApiUrl: Flow<String> by lazy {
        flowOf(remoteEndpoints.media.getEndpointPreset())
    }
}

fun RemoteApiEndpointRepositoryPreset(
    coroutineScope: CoroutineScope,
): RemoteApiEndpointRepository {
    return RemoteApiEndpointRepositoryPreset(
        RemoteEndpointsRepositoryPresetDefault()
    )
//    return RemoteApiEndpointRepositoryCompat()
}