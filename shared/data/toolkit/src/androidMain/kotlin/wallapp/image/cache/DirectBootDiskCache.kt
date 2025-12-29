package wallapp.image.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import wallapp.bitmap.saveBitmap
import wallapp.image.cache.DirectBootDiskCache.Companion.DIRECT_BOOT_DISK_CACHE_SUFFIX
import wallapp.log.Log
import wallapp.remoteassetfetcher.RemoteAssetFetcher.Companion.asHash
import wallapp.utils.deleteAllContents
import java.io.File

interface DirectBootDiskCache {

    suspend fun getBitmap(key: String): Bitmap?

    suspend fun putBitmap(key: String, bitmap: Bitmap): Boolean

    suspend fun bitmapFileExists(key: String): Boolean

    suspend fun clearCache()

    companion object {
        const val DIRECT_BOOT_DISK_CACHE_SUFFIX = "direct_boot_cache/"
    }
}

//@RequiresApi(Build.VERSION_CODES.N)
class DirectBootDiskCacheDefault(
    private val context: Context
) : DirectBootDiskCache {

    private val isAboveN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    override suspend fun getBitmap(key: String): Bitmap? {
        if (!isAboveN) return null

        log("[BitmapCaching] Try fetching bitmap from direct boot cache for key: %s", key)
        if (!File(key.cacheFilePath()).exists()) return null
        return BitmapFactory.decodeFile(key.cacheFilePath())
    }

    override suspend fun putBitmap(key: String, bitmap: Bitmap): Boolean {
        if (!isAboveN) return false

        log("[BitmapCaching] Saving bitmap in direct boot cache for key: %s", key)
        val path = key.cacheFilePath()
        return saveBitmap(path, bitmap)
    }

    override suspend fun bitmapFileExists(key: String): Boolean {
        if (!isAboveN) return false

        return File(key.cacheFilePath()).let {
            it.isFile && it.exists() && it.length() > 0
        }
    }

    override suspend fun clearCache() {
        if (isAboveN) {
            getCacheFolder().deleteAllContents()
        }
    }

    private fun getCacheFolder(): File =
        File(context.createDeviceProtectedStorageContext().cacheDir, DIRECT_BOOT_DISK_CACHE_SUFFIX)
            .apply { if (!exists()) { mkdir() } }

    private fun String.cacheFilePath(): String = getCacheFolder().absolutePath + "/" + asHash()

    private val enableLogging = false
    private fun log(message: String, vararg str: Any? = arrayOf<Any?>(null)) {
        if (enableLogging) {
            Log.d(message, *str)
        }
    }
}