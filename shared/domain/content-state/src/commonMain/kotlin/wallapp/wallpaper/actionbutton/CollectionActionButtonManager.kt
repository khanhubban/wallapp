package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.WallpaperId
import wallapp.data.collection.CollectionState
import wallapp.data.wallpaper.StaticWallpaperSize

interface CollectionActionButtonManager {

    fun getCollectionActionButtonState(
        wallpaperId: WallpaperId?,
        collectionState: StateFlow<CollectionState?>,
        staticWallpaperSize: StaticWallpaperSize,
    ): Flow<CollectionActionButtonState>

}