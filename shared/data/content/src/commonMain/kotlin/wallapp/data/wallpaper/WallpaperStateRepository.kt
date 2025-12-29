package wallapp.data.wallpaper

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId

interface WallpaperStateRepository {

    fun getWallpaperState(wallpaperId: RemixId): Flow<WallpaperState?>
}