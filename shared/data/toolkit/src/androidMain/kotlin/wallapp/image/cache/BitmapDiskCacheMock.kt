package wallapp.image.cache

import android.graphics.Bitmap

class BitmapDiskCacheMock : BitmapDiskCache {

    var mockCache = mutableMapOf<String, Bitmap>()

    override suspend fun getBitmap(key: String): Bitmap? {
        return mockCache[key]
    }

    override suspend fun putBitmap(key: String, bitmap: Bitmap): Boolean {
        mockCache[key] = bitmap
        return true
    }

    override suspend fun putBitmapDirectBoot(key: String, bitmap: Bitmap): Boolean {
        return false
    }

    override suspend fun clearDirectBootCache() {}

    override fun existsInCache(key: String): Boolean {
        return mockCache.containsKey(key)
    }
}