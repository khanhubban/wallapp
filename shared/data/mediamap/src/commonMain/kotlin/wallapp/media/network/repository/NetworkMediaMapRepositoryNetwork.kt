package wallapp.media.network.repository

import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.SerializationException
import wallapp.data.DataRepository
import wallapp.download.DownloadState
import wallapp.log.Log
import wallapp.media.network.model.NetworkMediaData
import wallapp.media.network.model.NetworkMediaMapResult
import wallapp.remoteapi.RemoteApi


class NetworkMediaMapRepositoryNetwork(
    private val config: NetworkMediaMapRepositoryConfig,
    private val remoteApi: RemoteApi,
    private val dataRepository: DataRepository,
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
        Log.d("[NRW-F] mMR, fetchDataNetwork() fetchCount: $fetchCount, endpointUrl: $endpointUrl")

        fun error(message: String): NetworkMediaMapResult {
            Log.w(message)
            return NetworkMediaMapResult.Error(message)
        }

        val content = try {
            val state = remoteApi.getFile(endpointUrl)
                .first { it is DownloadState.Success || it is DownloadState.Error }
            when (state) {
                is DownloadState.Error -> {
                    return error("Failed to download content from network for $endpointUrl: ${state.message}")
                }

                is DownloadState.Success -> {
                    val dataBlob = dataRepository.getDataBlob(state.dataHandle)
                        ?: return error("Failed to parse network content")
                    dataBlob.byteArray.decodeToString()
                }

                else -> {
                    return error("Failed to download content from network for $endpointUrl")
                }
            }
        } catch (e: IOException) {
            return error("IOException fetching content from network for $endpointUrl: ${e}, ${e.stackTraceToString()}")
        } catch (e: Exception) {
            return error("Exception fetching content from network for $endpointUrl: ${e}, ${e.stackTraceToString()}")
        }

        Log.v("[NRW-F] Fetched media map from network")

        return try {
            NetworkMediaMapResult.Success(
                networkMediaData = NetworkMediaData.fromJson(content),
                sourceId = endpointUrl,
            )
        } catch (e: SerializationException) {
            Log.e("Error parsing content from network: $e")
            return error("Error parsing content from network: $e")
        }
    }
}
