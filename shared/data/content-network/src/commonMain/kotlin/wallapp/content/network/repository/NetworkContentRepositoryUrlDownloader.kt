package wallapp.content.network.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.SerializationException
import wallapp.content.network.model.NetworkContent
import wallapp.data.DataRepository
import wallapp.download.DownloadState
import wallapp.log.Logger
import wallapp.remoteapi.RemoteApi
import wallapp.remoteendpoint.RemoteApiEndpointRepository


class NetworkContentRepositoryUrlDownloader(
    private val remoteApiEndpointRepository: RemoteApiEndpointRepository,
    private val remoteApi: RemoteApi,
    private val dataRepository: DataRepository,
) : NetworkContentRepository {

    companion object {
        val Log = Logger("[NRW] NetworkContentRepositoryUrlDownloader")
    }

    private val endpointUrlFlow: Flow<String>
        get() = remoteApiEndpointRepository.contentApiUrl

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchNetworkContent(forceRefresh: Boolean, forceCache: Boolean): Flow<NetworkContentResult> =
        endpointUrlFlow
            .filter { it.isNotBlank() }
            .flatMapLatest { endpointUrl ->
                downloadData(endpointUrl)
            }

    private fun downloadData(endPointPath: String): Flow<NetworkContentResult> {
        Log.d("[NRW-F] nC, downloadData: $endPointPath")
        return remoteApi.getFile(endPointPath).map { downloadState ->
            Log.d("downloadState: $downloadState")
            when (downloadState) {
                is DownloadState.Success -> {
                    try {
                        val dataBlob = dataRepository.getDataBlob(downloadState.dataHandle)
                            ?: return@map NetworkContentResult.Error("Failed to parse network content")
                        NetworkContent.fromExportString(dataBlob.byteArray.decodeToString())
                            .let { NetworkContentResult.Success(it) }
                    } catch (e: SerializationException) {
                        NetworkContentResult.Error("Error parsing network content: ${e.message}")
                    }
                }

                is DownloadState.Error -> NetworkContentResult.Error("Download error: ${downloadState.message}")

                else -> null
            }
        }.onEach {
            Log.d("[NRW-F] nC, result != null: ${it != null}")
        }.filterNotNull()
    }
}