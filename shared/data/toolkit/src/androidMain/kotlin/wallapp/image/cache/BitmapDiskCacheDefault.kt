package wallapp.image.cache

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.os.UserManagerCompat
import wallapp.bitmap.saveBitmap
import wallapp.image.cache.BitmapDiskCache.Companion.BITMAP_DISK_CACHE_SUFFIX
import wallapp.log.Log
import wallapp.remoteassetfetcher.RemoteAssetFetcher.Companion.asHash
import wallapp.utils.deleteAllContents
import java.io.File

class BitmapDiskCacheDefault(
    private val context: Context
) : BitmapDiskCache {

    private val isAboveN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    override suspend fun getBitmap(key: String): Bitmap? {
        Log.d("[BitmapCaching] Try fetching bitmap from disk cache for key: %s", key)
        if (!File(key.cacheFilePath()).exists()) return null
        return BitmapFactory.decodeFile(key.cacheFilePath())
    }

    override suspend fun putBitmap(key: String, bitmap: Bitmap): Boolean {
        Log.d("[BitmapCaching] Saving bitmap in disk cache for key: %s", key)
        val path = key.cacheFilePath()
        return saveBitmap(path, bitmap)
    }

    override suspend fun putBitmapDirectBoot(key: String, bitmap: Bitmap): Boolean {
        Log.d("[BitmapCaching] Saving bitmap in direct boot cache for key: %s", key)
        val path = key.cacheFilePath(true)
        return saveBitmap(path, bitmap)
    }

    override suspend fun clearDirectBootCache() {
        if (isAboveN) {
            getCacheFolder(true).deleteAllContents()
        }
    }

    override fun existsInCache(key: String): Boolean = File(key.cacheFilePath()).exists()

    @SuppressLint("NewApi")
    private fun getCacheFolder(diskBoot: Boolean = false): File =
        (if ((diskBoot || !UserManagerCompat.isUserUnlocked(context)) && isAboveN) {
            Log.d("[BitmapCaching] Retrieving direct boot cache folder")
            File(context.createDeviceProtectedStorageContext().cacheDir, BITMAP_DISK_CACHE_SUFFIX)
        } else {
            File(context.filesDir, BITMAP_DISK_CACHE_SUFFIX)
        }).apply { if (!exists()) { mkdir() } }

    private fun String.cacheFilePath(diskBoot: Boolean = false): String = getCacheFolder(diskBoot).absolutePath + "/" + asHash()
}