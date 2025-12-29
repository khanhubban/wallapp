package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object RemoteEndpointsRepositoryNetworkNoOp : RemoteEndpointsRepositoryNetwork {

    override fun getRemoteEndpoints(
        track: RemoteEndpointTrack,
        forceRefresh: Boolean,
        forceCache: Boolean
    ): Flow<RemoteEndpoints?> {
        return flowOf(null)
    }
}