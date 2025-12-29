package wallapp.search.network.repository

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
import wallapp.remoteapi.RemoteApi
import wallapp.search.model.NetworkSearchMetadata


class NetworkSearchContentRepositoryNetwork(
    private val config: NetworkSearchContentRepositoryConfig,
    private val remoteApi: RemoteApi,
    private val dataRepository: DataRepository,
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
        Log.d("[NRW-F] sC, fetchDataNetwork() fetchCount: $fetchCount, endpointUrl: $endpointUrl")

        val content = try {
            val state = remoteApi.getFile(endpointUrl)
                .first { it is DownloadState.Success || it is DownloadState.Error }
            when (state) {
                is DownloadState.Error -> {
                    return null
                }

                is DownloadState.Success -> {
                    val dataBlob = dataRepository.getDataBlob(state.dataHandle)
                        ?: return null
                    dataBlob.byteArray.decodeToString()
                }

                else -> {
                    return null
                }
            }
        } catch (e: IOException) {
            Log.w("Error fetching content from network [$endpointUrl]: $e")
            return null
        } catch (e: Exception) {
            Log.w("Error fetching content from network [$endpointUrl]: $e")
            return null
        }

        Log.v("[NRW-F] sC, Fetched content from network")
        return try {
            NetworkSearchMetadata.fromExportString(content)
        } catch (e: SerializationException) {
            Log.w("[NRW] sC, Error parsing content from network: ${e.message}")
            null
        }
    }
}