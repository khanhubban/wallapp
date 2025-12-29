package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import wallapp.content.model.Id

@OptIn(InternalCoroutinesApi::class)
class WallpaperSystemPhotoStatusCacheMemory : WallpaperSystemPhotoStatusCache {

    private val _all = MutableStateFlow<MutableList<WallpaperSystemPhotoStatusCacheEntry>>(
        mutableListOf()
    )
    override val all: Flow<List<WallpaperSystemPhotoStatusCacheEntry>>
        get() = _all

    private val cacheLock = SynchronizedObject()

    override fun add(cacheEntry: WallpaperSystemPhotoStatusCacheEntry) {
        synchronized(cacheLock) {
            val cache = _all.value
            val existingEntry = cache.find { it.id == cacheEntry.id }
            if (existingEntry != null) {
                cache.remove(existingEntry)
            }
            _all.value.add(cacheEntry)
        }
    }

    override fun remove(id: Id) {
        synchronized(cacheLock) {
            val cache = _all.value
            val existingEntry = cache.find { it.id == id }
            if (existingEntry != null) {
                cache.remove(existingEntry)
                _all.value = cache
            }
        }
    }

    override fun get(id: Id): WallpaperSystemPhotoStatusCacheEntry? {
        synchronized(cacheLock) {
            return _all.value.find { it.id == id }
        }
    }
}