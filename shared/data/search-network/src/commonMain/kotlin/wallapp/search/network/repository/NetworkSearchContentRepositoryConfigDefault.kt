package wallapp.search.network.repository

import kotlinx.coroutines.flow.Flow
import wallapp.remoteendpoint.RemoteApiEndpointRepository

class NetworkSearchContentRepositoryConfigDefault(
    private val remoteApiEndpointRepository: RemoteApiEndpointRepository,
) : NetworkSearchContentRepositoryConfig {

    override val endpointUrl: Flow<String>
        get() = remoteApiEndpointRepository.searchApiUrl
}

