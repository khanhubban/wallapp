package wallapp.search.network.repository

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
import wallapp.search.model.NetworkSearchMetadata


class NetworkSearchContentRepositoryKtor(
    private val config: NetworkSearchContentRepositoryConfig,
    private val httpClient: HttpClient,
) : NetworkSearchContentRepository {

    private val endpointUrlFlow: Flow<String>
        get() = config.endpointUrl

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchNetworkContent(forceRefresh: Boolean): Flow<NetworkSearchMetadata?> =
        endpointUrlFlow
            .filter { it.isNotBlank() }
            .mapLatest { endpointUrl ->
                fetchDataNetwork(endpointUrl)
            }

    private var fetchCount = 0
    private suspend fun fetchDataNetwork(endpointUrl: String): NetworkSearchMetadata? {
        fetchCount++
        Log.d("fetchDataNetwork() fetchCount: $fetchCount, endpointUrl: $endpointUrl")

        val response = try {
            val response = httpClient.get(endpointUrl)
            if (response.status.value != 200) {
                Log.w("Error fetching content from network for $endpointUrl: status: ${response.status}")
                return null
            } else {
                response
            }
        } catch (e: IOException) {
            Log.w("IOException fetching content from network for $endpointUrl: $e")
            return null
        } catch (e: Exception) {
            Log.w("Exception fetching content from network for $endpointUrl: $e")
            return null
        }

        Log.v("Fetched content from network: ${response.status}")
        val body = response.body<String>()
        return try {
            NetworkSearchMetadata.fromExportString(body)
        } catch (e: SerializationException) {
            Log.w("Error parsing content from network: ${e.message}")
            null
        }
    }
}
