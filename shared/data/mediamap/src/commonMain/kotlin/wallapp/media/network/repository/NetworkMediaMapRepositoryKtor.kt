package wallapp.media.network.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.SerializationException
import wallapp.log.Log
import wallapp.media.network.model.NetworkMediaData
import wallapp.media.network.model.NetworkMediaMapResult


class NetworkMediaMapRepositoryKtor(
    private val config: NetworkMediaMapRepositoryConfig,
    private val httpClient: HttpClient,
) : NetworkMediaMapRepository {

    private val endpointUrlFlow: Flow<String>
        get() = config.endpointUrl

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchNetworkMediaMap(forceRefresh: Boolean): Flow<NetworkMediaMapResult> =
        endpointUrlFlow
            .filter { it.isNotBlank() }
            .mapLatest { endpointUrl ->
                fetchDataNetwork(endpointUrl)
            }

    private var fetchCount = 0
    private suspend fun fetchDataNetwork(endpointUrl: String): NetworkMediaMapResult {
        fetchCount++
        Log.d("fetchDataNetwork() fetchCount: $fetchCount, endpointUrl: $endpointUrl")

        fun error(message: String): NetworkMediaMapResult {
            Log.w(message)
            return NetworkMediaMapResult.Error(message)
        }

        val response = try {
            val response = httpClient.get(endpointUrl)
            if (response.status.value != 200) {
                return error("Error fetching media map from network for $endpointUrl: status: ${response.status}")
            } else {
                response
            }
        } catch (e: IOException) {
            return error("IOException fetching media map from network for $endpointUrl: ${e}, ${e.stackTraceToString()}")
        } catch (e: Exception) {
            return error("Exception fetching media map from network for $endpointUrl: ${e}, ${e.stackTraceToString()}")
        }

        Log.v("Fetched media map from network: ${response.status}")
        val body = response.body<String>()
        return try {
            NetworkMediaMapResult.Success(
                networkMediaData = NetworkMediaData.fromJson(body),
                sourceId = endpointUrl,
            )
        } catch (e: SerializationException) {
            return error("Error parsing media map from network: $e")
        }
    }
}
