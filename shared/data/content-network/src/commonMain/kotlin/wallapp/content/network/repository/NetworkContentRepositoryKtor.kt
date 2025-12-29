package wallapp.content.network.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.SerializationException
import wallapp.content.network.model.NetworkContent
import wallapp.log.Log
import wallapp.network.httpclient.createNetworkHttpClient
import wallapp.remoteendpoint.RemoteApiEndpointRepository
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryPreset


class NetworkContentRepositoryKtor(
    private val remoteApiEndpointRepository: RemoteApiEndpointRepository,
    private val httpClient: HttpClient,
) : NetworkContentRepository {

    private val endpointUrlFlow: Flow<String>
        get() = remoteApiEndpointRepository.contentApiUrl

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchNetworkContent(forceRefresh: Boolean, forceCache: Boolean): Flow<NetworkContentResult> =
        endpointUrlFlow
            .filterNotNull()
            .mapLatest { endpointUrl ->
                fetchDataNetwork(endpointUrl)
            }

    private var fetchCount = 0
    private suspend fun fetchDataNetwork(endpointUrl: String): NetworkContentResult {
        fetchCount++
        Log.d("fetchDataNetwork() fetchCount: $fetchCount, endpointUrl: $endpointUrl")

        fun error(message: String): NetworkContentResult {
            Log.w(message)
            return NetworkContentResult.Error(message)
        }

        val response = try {
            val response = httpClient.get(endpointUrl)
            if (response.status.value != 200) {
                return error("Error fetching content from network for $endpointUrl: status: ${response.status}")
            } else {
                response
            }
        } catch (e: IOException) {
            return error("IOException fetching content from network for $endpointUrl: ${e}, ${e.stackTraceToString()}")
        } catch (e: Exception) {
            return error("Exception fetching content from network for $endpointUrl: ${e}, ${e.stackTraceToString()}")
        }

        Log.v("Fetched content from network: ${response.status}")
        val content = response.body<String>()
        return try {
            NetworkContentResult.Success(NetworkContent.fromExportString(content))
        } catch (e: SerializationException) {
            return error("Error parsing content from network: $e")
        }
    }
}

fun NetworkContentRepositoryKtorPreset(
    coroutineScope: CoroutineScope,
    remoteApiEndpointRepository: RemoteApiEndpointRepository = RemoteApiEndpointRepositoryPreset(coroutineScope),
    httpClient: HttpClient = createNetworkHttpClient(),
): NetworkContentRepository = NetworkContentRepositoryKtor(remoteApiEndpointRepository, httpClient)