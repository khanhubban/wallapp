package wallapp.remoteendpoint

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.coroutine.collectIn
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.remotecontent.RemoteServerContentCache
import wallapp.util.combine

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteApiEndpointRepositoryDefault(
    private val config: RemoteApiEndpointRepositoryConfig,
    remoteEndpointsRepositoryNetwork: RemoteEndpointsRepositoryNetwork,
    private val networkErrorBroadcaster: NetworkErrorBroadcaster,
    networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster,
    private val remoteServerContentCache: RemoteServerContentCache,
    coroutineScopeIo: CoroutineScope,
) : RemoteApiEndpointRepository {

    companion object {
        val Log = RemoteEndpointLog
    }

    private val currentTrack: StateFlow<RemoteEndpointTrack>
        get() = config.remoteEndpointTrack

    val remoteEndpointsFromNetwork = MutableStateFlow<RemoteEndpoints?>(null)

    val remoteEndpointsFromCache: StateFlow<RemoteEndpoints?> =
        remoteServerContentCache.endPoints
            .onEach {
                Log.d("[NCache] rAE: ${it.isNotBlank()}")
            }
            .map { contentString ->
                if (contentString.isBlank()) {
                    null
                } else {
                    RemoteEndpoints.from(contentString).also {
                        Log.d("** combine: network=$it")
                    }
                }
            }
            .stateIn(
                coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )

    private val remoteEndpoints: Flow<RemoteEndpoints>
        get() = remoteEndpointsFromCache.filterNotNull()

    override val contentApiUrl: Flow<String> by lazy {
        remoteEndpoints.map { it.content }
    }

    override val searchApiUrl: Flow<String> by lazy {
        remoteEndpoints.map { it.search }
    }

    override val mediaMapApiUrl: Flow<String> by lazy {
        combine(
            remoteEndpoints,
            config.imageHostPlatform,
            config.imageBucketSpec,
        ) { remoteEndpoints, imageHostPlatform, imageBucketSpec ->
            remoteEndpoints.media.getEndpoint(imageHostPlatform.key, imageBucketSpec.key)
        }
    }

    init {
        networkRefreshTriggerBroadcaster.remoteEndpointsRefresh.flatMapLatest {
            Log.d("[NRW-F] rAE, refresh")
            val currentTrack = currentTrack.value
            remoteEndpointsRepositoryNetwork.getRemoteEndpoints(
                currentTrack,
                forceRefresh = true,
            ).onEach {
                if (it == null) {
                    if (remoteServerContentCache.endPoints.value.isBlank()) {
                        networkErrorBroadcaster.reportNetworkError("rAE")
                    }
                } else {
                    remoteEndpointsFromNetwork.value = it
                    remoteServerContentCache.endPoints.value = it.exportString
                }
            }
        }.launchIn(coroutineScopeIo)

        remoteEndpoints.collectIn(coroutineScopeIo) {
            Log.d("** remoteEndpoints: $it")
        }
        mediaMapApiUrl.collectIn(coroutineScopeIo) {
            Log.d("[MediaMap] ** mediaMapApiUrl: $it")
        }
//        contentApiUrl.collectIn(coroutineScopeMain) {
//            RemoteEndpointLog.d("contentApiUrl: $it")
//        }
    }
}