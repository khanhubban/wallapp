package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow

interface RemoteApiEndpointRepository {

    val contentApiUrl: Flow<String>

    val searchApiUrl: Flow<String>

    val mediaMapApiUrl: Flow<String>
}