package wallapp.image.hash.blur

import org.jetbrains.skia.Bitmap
import wallapp.image.hash.ImageHashBitmapMapperDesktop
import wallapp.util.WeakReference

private data class CacheKeySkiaBitmap(
    val hash: String,
    val width: Int,
    val height: Int,
    val punch: Float,
)

class BlurHashDecoderBitmapDesktopDefault(
    private val decoder: BlurHashDecoder,
    private val bitmapMapper: ImageHashBitmapMapperDesktop,
) : BlurHashDecoderBitmapDesktop {

    private val cache = HashMap<CacheKeySkiaBitmap, WeakReference<Bitmap>>()

    override fun decode(
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
            val cached = cache[key]?.get()
            if (cached != null) {
//                Log.v("[BlurHashDecoderCached] decode: cache hit, ${key.width}x${key.height}, ${key.hash}")
                return cached
            }
        }

        return decoder.decode(blurHash, width, height, punch, useCache = false)
            ?.let { decoded ->
                bitmapMapper.map(decoded)?.also { bitmap ->
                    cache[key] = WeakReference(bitmap)
//                    Log.d("[BlurHashDecoderCached] decode: cache miss, ${key.width}x${key.height}, ${key.hash}")
                }
            }
    }

    override fun clearCache() {
        decoder.clearCache()
        cache.clear()
    }
}