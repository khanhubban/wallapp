package wallapp.image.hash.blur

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.Bitmap
import wallapp.image.hash.ImageHashBitmapMapperIos
import wallapp.util.WeakReference


class BlurHashDecoderBitmapIosSynchronized(
    private val decoder: BlurHashDecoder,
    private val bitmapMapper: ImageHashBitmapMapperIos,
) : BlurHashDecoderBitmapIos {

    private val cache = HashMap<CacheKeySkiaBitmap, WeakReference<Bitmap>>()
    private val cacheMutex = Mutex() // For synchronizing access to the cache

    override suspend fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float,
        useCache: Boolean,
    ): Bitmap? {
        if (blurHash == null || blurHash.length < 6) {
            return null
        }

        val key = CacheKeySkiaBitmap(
            hash = blurHash,
            width = width,
            height = height,
            punch = punch,
        )

        if (useCache) {
            val cached = cacheMutex.withLock {
                cache[key]?.get()
            }
            if (cached != null) {
//                Log.v("[BlurHashDecoderCached] decode: cache hit, ${key.width}x${key.height}, ${key.hash}")
                return cached
            }
        }

        val decodedBitmap = decoder.decode(blurHash, width, height, punch, useCache = false)?.let { decoded ->
            bitmapMapper.map(decoded)
        } ?: return null

        cacheMutex.withLock {
            cache[key] = WeakReference(decodedBitmap)
        }

//        Log.d("[BlurHashDecoderCached] decode: cache miss, ${key.width}x${key.height}, ${key.hash}")
        return decodedBitmap
    }

    override suspend fun clearCache() {
        cacheMutex.withLock {
            decoder.clearCache()
            cache.clear()
        }
    }
}
