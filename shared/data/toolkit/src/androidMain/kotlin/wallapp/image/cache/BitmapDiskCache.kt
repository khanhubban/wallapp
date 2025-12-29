package wallapp.image.cache

import android.graphics.Bitmap

interface BitmapDiskCache {

    suspend fun getBitmap(key: String): Bitmap?

    suspend fun putBitmap(key: String, bitmap: Bitmap): Boolean

    suspend fun putBitmapDirectBoot(key: String, bitmap: Bitmap): Boolean

    suspend fun clearDirectBootCache()

    fun existsInCache(key: String): Boolean

    companion object {
        const val BITMAP_DISK_CACHE_SUFFIX = "asset_fetcher_cache/"
    }
}

