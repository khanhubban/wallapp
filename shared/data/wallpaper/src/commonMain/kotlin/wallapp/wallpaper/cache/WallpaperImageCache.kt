package wallapp.wallpaper.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import wallapp.content.model.Id.RemixId
import wallapp.data.DataHandle
import wallapp.data.wallpaper.StaticWallpaperSize

interface WallpaperImageCache {

    val enabled: Boolean

    suspend fun putStaticWallpaper(
        id: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        dataHandle: DataHandle,
    ): Boolean

    fun getStaticWallpaperCacheStatus(id: RemixId, staticWallpaperSize: StaticWallpaperSize): Flow<WallpaperCacheStatus>

    suspend fun getStaticWallpaper(id: RemixId, staticWallpaperSize: StaticWallpaperSize): DataHandle? {
        return getStaticWallpaperCacheStatus(id, staticWallpaperSize)
            .firstOrNull { it is WallpaperCacheStatus.Cached }
            .let { (it as? WallpaperCacheStatus.Cached)?.dataHandle }
    }

    fun isCached(id: RemixId, staticWallpaperSize: StaticWallpaperSize): Boolean

    fun clearCache()
}

fun RemixId.imageCacheKey(staticWallpaperSize: StaticWallpaperSize): String
        = "img_${this.name}_${staticWallpaperSize.key}"