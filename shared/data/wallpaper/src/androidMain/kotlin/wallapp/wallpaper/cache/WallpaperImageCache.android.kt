package wallapp.wallpaper.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import wallapp.bitmap.BitmapMapper.toBitmap
import wallapp.bitmap.BitmapMapper.toByteArray
import wallapp.content.model.Id.RemixId
import wallapp.coroutine.CoroutineContexts
import wallapp.data.DataHandle
import wallapp.data.DataRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.image.cache.BitmapDiskCache

class WallpaperImageCacheAndroid(
    private val diskCache: BitmapDiskCache,
    private val dataRepository: DataRepository,
    private val coroutineScopeIo: CoroutineScope,
    private val coroutineContexts: CoroutineContexts,
) : WallpaperImageCache {

    override val enabled: Boolean
        get() = true

    override suspend fun putStaticWallpaper(
        id: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        dataHandle: DataHandle,
    ): Boolean {
        val key = id.imageCacheKey(staticWallpaperSize)
        val data = dataRepository.getDataBlob(dataHandle) ?: return false
        return diskCache.putBitmap(key, data.byteArray.toBitmap())
    }

    override fun getStaticWallpaperCacheStatus(
        id: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
    ): Flow<WallpaperCacheStatus> {
        return flow {
            emit(WallpaperCacheStatus.Loading)
            val key = id.imageCacheKey(staticWallpaperSize)
            val bitmap = diskCache.getBitmap(key)
            val dataHandle = bitmap?.toByteArray()?.let { DataHandle.fromBytesCompat(it) }
            if (dataHandle != null) {
                emit(WallpaperCacheStatus.Cached(dataHandle))
            } else {
                emit(WallpaperCacheStatus.NotCached)
            }
        }.flowOn(coroutineContexts.io)
    }

    override fun isCached(id: RemixId, staticWallpaperSize: StaticWallpaperSize): Boolean {
        val key = id.imageCacheKey(staticWallpaperSize)
        return diskCache.existsInCache(key)
    }

    override fun clearCache() {
        coroutineScopeIo.launch {
            diskCache.clearDirectBootCache()
        }
    }
}