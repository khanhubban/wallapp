package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow

interface RemoteEndpointsRepositoryNetwork {

    fun getRemoteEndpoints(track: RemoteEndpointTrack, forceRefresh: Boolean = false, forceCache: Boolean = false): Flow<RemoteEndpoints?>

}