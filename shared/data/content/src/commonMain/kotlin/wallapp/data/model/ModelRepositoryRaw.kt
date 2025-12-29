package wallapp.data.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.content.network.map.NetworkContentMapper
import wallapp.content.network.model.NetworkContent
import wallapp.content.network.repository.NetworkContentRepository
import wallapp.content.network.repository.NetworkContentResult
import wallapp.data.artist.Artist
import wallapp.data.folder.Folder
import wallapp.data.util.allCategories
import wallapp.data.util.allRemixes
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.remotecontent.RemoteServerContentCache
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryDefault.Companion.Log

@OptIn(ExperimentalCoroutinesApi::class)
class ModelRepositoryRaw(
    private val networkContentRepository: NetworkContentRepository,
    private val networkContentMapper: NetworkContentMapper,
    private val networkErrorBroadcaster: NetworkErrorBroadcaster,
    networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster,
    remoteServerContentCache: RemoteServerContentCache,
    private val coroutineScopeIo: CoroutineScope,
) : ModelRepository {

    val networkContentFromNetwork = MutableStateFlow<NetworkContent?>(null)

    // This needs to be a StateFlow as its emissions are being shared in this class itself
    val networkContentFromCache: StateFlow<NetworkContent?> by lazy {
        remoteServerContentCache.baseContent
            .onEach { Log.d("[NCache] nC: ${it.isNotBlank()}") }
            .filter { it.isNotBlank() }
            .map { contentString ->
                contentString.let { NetworkContent.fromExportString(it) }
            }.stateIn(
                coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = null
            )
    }

    override val allWallpaperItems: StateFlow<List<WallpaperItem>> by lazy {
        networkContentFromCache
            .map { content ->
                content?.let { networkContentMapper.mapAllWallpaperItems(it) } ?: emptyList()
            }
            .stateIn(
                scope = coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )
    }

    override val allRemixes: Flow<List<WallpaperRemix>> by lazy {
        allWallpaperItems.map { it.allRemixes ?: emptyList() }
    }

    override val allCategories: Flow<List<WallpaperCategory>> by lazy {
        allWallpaperItems.map { it.allCategories ?: emptyList() }
    }

    override val allArtists: Flow<List<Artist>> by lazy {
        networkContentFromCache
            .map { content ->
                content?.let { networkContentMapper.mapAllArtists(it) } ?: emptyList()
            }
    }

    override val allFolders: Flow<List<Folder>> by lazy {
        networkContentFromCache
            .map { content ->
                content?.let { networkContentMapper.mapAllFolders(it) } ?: emptyList()
            }
    }

    init {
        networkRefreshTriggerBroadcaster.networkContentRefresh.flatMapLatest {
            Log.d("[NRW] [NRW-F] nC, refresh")
            networkContentRepository.fetchNetworkContent(forceRefresh = true)
        }.map { result ->
            val networkContent = if (result is NetworkContentResult.Success) {
                result.networkContent
            } else {
                null
            }
            if (networkContent == null) {
                if (remoteServerContentCache.baseContent.value.isEmpty()) {
                    networkErrorBroadcaster.reportNetworkError("nC")
                }
            } else {
                networkContentFromNetwork.value = networkContent
                remoteServerContentCache.baseContent.value = networkContent.exportString
            }
            networkContent
        }.onEach {
            Log.d("[NRW] nC != null: ${it != null}")
        }.launchIn(coroutineScopeIo)
    }
}