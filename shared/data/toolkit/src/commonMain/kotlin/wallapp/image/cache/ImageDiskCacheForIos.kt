package wallapp.image.cache

import com.seiko.imageloader.cache.disk.DiskCache
import okio.BufferedSource
import okio.FileSystem
import okio.buffer
import okio.use
import wallapp.log.Log

interface ImageDiskCacheForIos {

    fun get(key: String): ByteArray?
    fun put(key: String, data: ByteArray)
    fun remove(key: String)
    fun isCached(key: String): Boolean
    fun clear()
}

class ImageDiskCacheForIosDefault(
    private val diskCache: DiskCache
) : ImageDiskCacheForIos {

    private val fileSystem: FileSystem get() = diskCache.fileSystem

    override fun get(key: String): ByteArray? {
        Log.d("[ImageDiskCacheForIos] get: $key")
        diskCache.openSnapshot(key)?.use { snapshot ->
            return snapshot.source().use { source ->
                source.readByteArray()
            }
        }
        return null
    }

    override fun put(key: String, data: ByteArray) {
        Log.d("[ImageDiskCacheForIos] put: $key")
        diskCache.openEditor(key)?.let { editor ->
            Log.d("[ImageDiskCacheForIos] path: ${editor.data}")
            fileSystem.write(editor.data) {
                write(data)
            }
            editor.commit()
        }
    }

    override fun remove(key: String) {
        Log.d("[ImageDiskCacheForIos] remove: $key")
        diskCache.remove(key)
    }

    override fun isCached(key: String): Boolean {
        val snapshot = diskCache.openSnapshot(key)
        val isCached = snapshot != null
        Log.d("[ImageDiskCacheForIos] isCached: key=$key, isCached=$isCached")
        snapshot?.close()
        return isCached
    }

    override fun clear() {
        diskCache.clear()
    }

    private fun DiskCache.Snapshot.source(): BufferedSource {
        return fileSystem.source(data).buffer()
    }
}
