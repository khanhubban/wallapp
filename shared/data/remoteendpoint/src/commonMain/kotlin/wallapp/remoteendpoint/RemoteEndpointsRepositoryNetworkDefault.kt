package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.SerializationException
import wallapp.data.DataRepository
import wallapp.download.DownloadState
import wallapp.remoteapi.RemoteApi
import wallapp.remoteapi.RemoteEndpointsSpec
import wallapp.remoteapi.RemoteEndpointsSpecRepository
import wallapp.string.clamp
import wallapp.string.quote

class RemoteEndpointsRepositoryNetworkDefault(
    private val remoteEndpointsSpecRepository: RemoteEndpointsSpecRepository,
    private val remoteApi: RemoteApi,
    private val dataRepository: DataRepository,
) : RemoteEndpointsRepositoryNetwork {

    val Log = RemoteEndpointLog

    private val cacheMap = mutableMapOf<RemoteEndpointTrack, RemoteEndpoints?>()

    private fun fetchNetworkContent(url: String): Flow<RemoteEndpointsNetworkResult> {
        Log.i("[NRW-F] rAE, fetchNetworkContent(): $url")
        return remoteApi.getFile(url).map { downloadState ->
            Log.d("downloadState: ${downloadState.toString().clamp(1024)}")
            when (downloadState) {
                is DownloadState.Success -> {
                    try {
                        val dataBlob = dataRepository.getDataBlob(downloadState.dataHandle)
                            ?: return@map RemoteEndpointsNetworkResult.Error("Failed to parse network content")
                        RemoteEndpoints.from(dataBlob.byteArray.decodeToString())
                            .let { RemoteEndpointsNetworkResult.Success(it) }
                    } catch (e: SerializationException) {
                        RemoteEndpointsNetworkResult.Error("Error parsing network content: ${e.message.quote()}")
                    }
                }

                is DownloadState.Error -> {
                    RemoteEndpointsNetworkResult.Error("Download error: ${downloadState.message}")
                }

                else -> null
            }
        }.filterNotNull()
    }

    private fun fetchRemoteEndpoints(url: String, track: RemoteEndpointTrack): Flow<RemoteEndpoints?> {
        return fetchNetworkContent(url)
            .map { result ->
                when (result) {
                    is RemoteEndpointsNetworkResult.Success -> result.remoteEndpoints.also {
                        cacheMap[track] = it
                    }
                    is RemoteEndpointsNetworkResult.Error -> {
                        val cachedResult = cacheMap[track]
                        if (cachedResult != null) {
                            cachedResult
                        } else {
                            Log.e("fetchRemoteEndpoints error: ${result.errorMessage}")
                            null
                        }
                    }
                }
            }
    }

    private val remoteEndpointsSpec: RemoteEndpointsSpec
        get() = remoteEndpointsSpecRepository.remoteEndpointsSpec

    private fun getEndpointSpecUrl(track: RemoteEndpointTrack): String {
        return remoteEndpointsSpec.getUrlForTrack(track)
    }

    override fun getRemoteEndpoints(
        track: RemoteEndpointTrack,
        forceRefresh: Boolean,
        forceCache: Boolean
    ): Flow<RemoteEndpoints?> {
        if (forceCache) {
            return flowOf(cacheMap[track])
        }
        if (forceRefresh) {
            cacheMap[track] = null
            return fetchRemoteEndpoints(getEndpointSpecUrl(track), track)
        }
        return merge(
            flowOf(cacheMap[track]),
            fetchRemoteEndpoints(getEndpointSpecUrl(track), track)
        )
    }
}
