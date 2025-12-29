package wallapp.search.content

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.annotation.VisibleForTesting
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.RemixId
import wallapp.log.Log
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.remotecontent.RemoteServerContentCache
import wallapp.search.content.SearchContentMapper.allSearchArtistMetadata
import wallapp.search.content.SearchContentMapper.allSearchFolderMetadata
import wallapp.search.content.SearchContentMapper.allSearchRemixMetadata
import wallapp.search.model.NetworkSearchMetadata
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFolderMetadata
import wallapp.search.model.SearchRemixMetadata
import wallapp.search.network.repository.NetworkSearchContentRepository

@Suppress("OPT_IN_USAGE")
class SearchContentRepositoryDefault(
    private val networkSearchContentRepository: NetworkSearchContentRepository,
    private val networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster,
    private val remoteServerContentCache: RemoteServerContentCache,
    coroutineScopeIo: CoroutineScope,
): SearchContentRepository {

    val searchContentFromCache: StateFlow<NetworkSearchMetadata?> by lazy {
        remoteServerContentCache.searchContent
            .onEach { Log.d("[NCache] sC: ${it.isNotBlank()}") }
            .filter { it.isNotBlank() }
            .map { contentString ->
                contentString.let { NetworkSearchMetadata.fromExportString(it) }
            }.stateIn(
                coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )
    }

    private val fetchNetworkContent: Flow<NetworkSearchMetadata> =
        searchContentFromCache.filterNotNull()

    override val allSearchRemixMetadata: Flow<List<SearchRemixMetadata>> =
        fetchNetworkContent
            .map { it.allSearchRemixMetadata }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSearchRemixMetadata(id: RemixId): Flow<SearchRemixMetadata?> {
        return allSearchRemixMetadata
            .mapLatest { list -> list.find { it.remixId == id } }
            .distinctUntilChanged()
    }

    override val allSearchArtistMetadata: Flow<List<SearchArtistMetadata>> =
        fetchNetworkContent
            .map { it.allSearchArtistMetadata }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSearchArtistMetadata(id: ArtistId): Flow<SearchArtistMetadata?> {
        return allSearchArtistMetadata
            .mapLatest { list -> list.find { it.artistId == id } }
            .distinctUntilChanged()
    }

    override val allSearchFolderMetadata: Flow<List<SearchFolderMetadata>> =
        fetchNetworkContent
            .map { it.allSearchFolderMetadata }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSearchFolderMetadata(id: Id.FolderId): Flow<SearchFolderMetadata?> {
        return allSearchFolderMetadata
            .mapLatest { list -> list.find { it.folderId == id } }
            .distinctUntilChanged()
    }

    @VisibleForTesting
    val isReady: StateFlow<Boolean> =
        allSearchRemixMetadata
            .map { it.isNotEmpty() }
            .stateIn(coroutineScopeIo, SharingStarted.Eagerly, false)

    init {
        networkRefreshTriggerBroadcaster.searchContentRefresh.flatMapLatest {
            Log.d("[NRW-F] sC, refresh")
            networkSearchContentRepository.fetchNetworkContent(forceRefresh = true)
        }.onEach {
            if (it != null) {
                remoteServerContentCache.searchContent.value = it.exportString
            }
        }.stateIn(
            scope = coroutineScopeIo,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        ).launchIn(coroutineScopeIo)
    }
}