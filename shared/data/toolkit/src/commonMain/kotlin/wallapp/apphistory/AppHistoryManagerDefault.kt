package wallapp.apphistory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import wallapp.coroutine.CoroutineScopes
import wallapp.coroutine.collectIn
import wallapp.log.Logger

class AppHistoryManagerDefault(
    private val cache: AppHistoryManagerCache,
    private val coroutineScopes: CoroutineScopes,
) : AppHistoryManager {

    companion object {
        val Log = Logger("AppHistoryManager")
        const val Separator = "<||>"
    }

    private val coroutineScopeIo: CoroutineScope
        get() = coroutineScopes.io


    override val recentlyViewedWallpaperIds: MutableStateFlow<List<Id>> =
        MutableStateFlow(emptyList())
    override val recentlyViewedCollectionIds: MutableStateFlow<List<Id>> =
        MutableStateFlow(emptyList())
    override val recentlyViewedArtistIds: MutableStateFlow<List<Id>> = MutableStateFlow(emptyList())

    //Wallpapers Data
    private val cachedRecentlyViewedWallpapers: MutableStateFlow<String>
        get() = cache.recentlyViewedWallpaperIds

    private val cachedRecentlyViewedWallpaperData: List<String>
        get() = cachedRecentlyViewedWallpapers.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedRecentlyViewedWallpaperIds: List<Id>
        get() = cachedRecentlyViewedWallpaperData
            .map { Id.fromExportShortString(it) }
            .toList()

    // Collections data
    private val cachedRecentlyViewedCollections: MutableStateFlow<String>
        get() = cache.recentlyViewedCollectionIds
    private val cachedRecentlyViewedCollectionData: List<String>
        get() = cachedRecentlyViewedCollections.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedRecentlyViewedCollectionIds: List<Id>
        get() = cachedRecentlyViewedCollectionData
            .map { Id.fromExportShortString(it) }
            .toList()

    // Artists data
    private val cachedRecentlyViewedArtists: MutableStateFlow<String>
        get() = cache.recentlyViewedArtistIds
    private val cachedRecentlyViewedArtistData: List<String>
        get() = cachedRecentlyViewedArtists.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedRecentlyViewedArtistIds: List<Id>
        get() = cachedRecentlyViewedArtistData
            .map { Id.fromExportShortString(it) }
            .toList()

    init {
        cachedRecentlyViewedWallpapers.collectIn(coroutineScopeIo) { _ ->
            recentlyViewedWallpaperIds.value = cachedRecentlyViewedWallpaperIds
                .also { ids ->
                    Log.d("init(): recentlyViewedWallpaperIds: ${ids.joinToString(Separator) { it.exportString }}")
                }
        }
        cachedRecentlyViewedCollections.collectIn(coroutineScopeIo) { _ ->
            recentlyViewedCollectionIds.value = cachedRecentlyViewedCollectionIds
                .also { ids ->
                    Log.d("init(): recentlyViewedCollectionIds: ${ids.joinToString(Separator) { it.exportString }}")
                }
        }
        cachedRecentlyViewedArtists.collectIn(coroutineScopeIo) { _ ->
            recentlyViewedArtistIds.value = cachedRecentlyViewedArtistIds
                .also { ids ->
                    Log.d("init(): recentlyViewedArtistIds: ${ids.joinToString(Separator) { it.exportString }}")
                }
        }
    }


    private fun updateCachedRecentlyViewedWallpapers(string: String) {
        cachedRecentlyViewedWallpapers.value = string
    }

    private fun updateCachedRecentlyViewedCollections(string: String) {
        cachedRecentlyViewedCollections.value = string
    }

    private fun updateCachedRecentlyViewedArtists(string: String) {
        cachedRecentlyViewedArtists.value = string
    }

    override fun registerWallpaperView(wallpaperId: RemixId) {
        val updatedRecentlyViewedWallpaperIds = mutableListOf<Id>().apply {
            add(wallpaperId)
            addAll(recentlyViewedWallpaperIds.value)
        }.distinct()
        updateCachedRecentlyViewedWallpapers(updatedRecentlyViewedWallpaperIds.joinToString(Separator) { it.exportString })
        Log.d("registerWallpaperView(): wallpaperId: $wallpaperId List: ${updatedRecentlyViewedWallpaperIds.joinToString(Separator) { it.exportString }}")
    }

    override fun registerCollectionView(collectionId: CollectionId, topmostWallpaperId: RemixId?) {
        val updatedRecentlyViewedCollectionIds = mutableListOf<Id>().apply {
            add(collectionId)
            addAll(recentlyViewedCollectionIds.value)
        }.distinct()

        updateCachedRecentlyViewedCollections(updatedRecentlyViewedCollectionIds.joinToString(Separator) { it.exportString })

        topmostWallpaperId?.let { registerWallpaperView(it) }

        Log.d("registerCollectionView(): collectionId: $collectionId, topmostWallpaperId: $topmostWallpaperId List: ${updatedRecentlyViewedCollectionIds.joinToString(Separator) { it.exportString }}")
    }

    override fun registerArtistView(artistId: ArtistId) {
        val updatedRecentlyViewedArtistIds = mutableListOf<Id>().apply {
            add(artistId)
            addAll(recentlyViewedArtistIds.value)
        }.distinct()

        updateCachedRecentlyViewedArtists(updatedRecentlyViewedArtistIds.joinToString(Separator) { it.exportString })
        Log.d("registerArtistView(): artistId: $artistId List: ${updatedRecentlyViewedArtistIds.joinToString(Separator) { it.exportString }}")
    }

    override fun registerWallpaperDownload(wallpaperId: RemixId) {
        Log.d("registerWallpaperDownload(): wallpaperId: $wallpaperId")
    }

    override fun registerWallpaperSet(wallpaperId: RemixId) {
        Log.d("registerWallpaperSet(): wallpaperId: $wallpaperId")
    }
}
