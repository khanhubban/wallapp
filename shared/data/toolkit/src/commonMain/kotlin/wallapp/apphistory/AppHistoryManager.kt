package wallapp.apphistory

import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId

interface AppHistoryManager {

    val recentlyViewedWallpaperIds: StateFlow<List<Id>>
    val recentlyViewedCollectionIds: StateFlow<List<Id>>
    val recentlyViewedArtistIds: StateFlow<List<Id>>

    fun registerCollectionView(collectionId: CollectionId, topmostWallpaperId: RemixId?)
    fun registerArtistView(artistId: ArtistId)
    fun registerWallpaperView(wallpaperId: RemixId)

    /**
     * Register a wallpaper download event.
     */
    fun registerWallpaperDownload(wallpaperId: RemixId)

    /**
     * Register a wallpaper set event. Will only be true on Android.
     */
    fun registerWallpaperSet(wallpaperId: RemixId)
}

