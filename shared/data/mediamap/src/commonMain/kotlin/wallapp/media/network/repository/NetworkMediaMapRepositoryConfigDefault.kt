package wallapp.media.network.repository

import kotlinx.coroutines.flow.Flow
import wallapp.remoteendpoint.RemoteApiEndpointRepository

class NetworkMediaMapRepositoryConfigDefault(
    private val remoteApiEndpointRepository: RemoteApiEndpointRepository
) : NetworkMediaMapRepositoryConfig {

    override val endpointUrl: Flow<String>
        get() = remoteApiEndpointRepository.mediaMapApiUrl
}