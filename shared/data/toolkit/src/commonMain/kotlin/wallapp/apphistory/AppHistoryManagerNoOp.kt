package wallapp.apphistory

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId

class AppHistoryManagerNoOp : AppHistoryManager {
    override val recentlyViewedWallpaperIds: StateFlow<List<Id>> = MutableStateFlow(emptyList())
    override val recentlyViewedCollectionIds: StateFlow<List<Id>> = MutableStateFlow(emptyList())
    override val recentlyViewedArtistIds: StateFlow<List<Id>> = MutableStateFlow(emptyList())

    override fun registerCollectionView(
        collectionId: CollectionId,
        topmostWallpaperId: RemixId?,
    ) {
    }

    override fun registerArtistView(artistId: ArtistId) {
    }

    override fun registerWallpaperView(wallpaperId: RemixId) {
    }

    override fun registerWallpaperDownload(wallpaperId: RemixId) {
    }

    override fun registerWallpaperSet(wallpaperId: RemixId) {
    }
}