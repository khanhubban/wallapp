package wallapp.wallpaper.cache

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import wallapp.content.model.Id
import wallapp.data.DataHandle
import wallapp.data.DataRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.log.Log
import kotlin.coroutines.resume

class WallpaperImageCacheIos(
    private val baseCache: BaseCache,
    private val dataRepository: DataRepository,
) : WallpaperImageCache {

    override val enabled: Boolean
        get() = true

    override suspend fun putStaticWallpaper(
        id: Id.RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        dataHandle: DataHandle,
    ): Boolean {
        val data = dataRepository.getDataBlob(dataHandle)?.byteArray ?: return false
        return suspendCancellableCoroutine { continuation ->
            baseCache.putData(
                id.imageCacheKey(staticWallpaperSize),
                data,
            ) { result ->
                Log.d("[WallpaperImageCacheIos] putStaticWallpaper id: $id, result: $result, isActive: ${continuation.isActive}")
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }
        }
    }

    override fun getStaticWallpaperCacheStatus(
        id: Id.RemixId,
        staticWallpaperSize: StaticWallpaperSize,
    ): Flow<WallpaperCacheStatus> {
        return callbackFlow {
            trySend(WallpaperCacheStatus.Loading)
            baseCache.getData(id.imageCacheKey(staticWallpaperSize)) { result ->
                Log.d("[WallpaperImageCacheIos] getStaticWallpaper id: $id, result: ${result?.size}")
                val dataHandle = result?.let { DataHandle.fromBytesCompat(it) }
                if (dataHandle != null) {
                    trySend(WallpaperCacheStatus.Cached(dataHandle))
                } else {
                    trySend(WallpaperCacheStatus.NotCached).onClosed {  }
                }
                close()
            }
            awaitClose {  }
        }
    }

    override fun isCached(id: Id.RemixId, staticWallpaperSize: StaticWallpaperSize): Boolean {
        val isCached = baseCache.isCached(id.imageCacheKey(staticWallpaperSize))
        Log.d("[WallpaperImageCacheIos] isCached id: $id, result: $isCached")
        return isCached
    }

    override fun clearCache() {
        baseCache.clearCache()
    }
}