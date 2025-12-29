package wallapp.wallpaper.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import wallapp.content.model.Id
import wallapp.data.DataHandle
import wallapp.data.wallpaper.StaticWallpaperSize

object WallpaperImageCacheNoOp : WallpaperImageCache {

    override val enabled: Boolean
        get() = false

    override suspend fun putStaticWallpaper(
        id: Id.RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        dataHandle: DataHandle,
    ): Boolean = false

    override fun getStaticWallpaperCacheStatus(
        id: Id.RemixId,
        staticWallpaperSize: StaticWallpaperSize
    ): Flow<WallpaperCacheStatus> = flow {}


    override fun isCached(id: Id.RemixId, staticWallpaperSize: StaticWallpaperSize): Boolean = false

    override fun clearCache() {}
}
