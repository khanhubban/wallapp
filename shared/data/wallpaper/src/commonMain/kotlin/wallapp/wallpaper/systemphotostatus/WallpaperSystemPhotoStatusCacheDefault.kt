package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id
import wallapp.log.Log


class WallpaperSystemPhotoStatusCacheDefault(
    private val sourceData: WallpaperSystemPhotoStatusCacheDefaultData,
    coroutineScopeIo: CoroutineScope,
) : WallpaperSystemPhotoStatusCache {

    private val sourceDataStringCache: MutableStateFlow<String>
        get() = sourceData.allCache

    override val all: StateFlow<List<WallpaperSystemPhotoStatusCacheEntry>>
        = sourceDataStringCache
            .map {
                mapWallpaperSystemPhotoStatusCacheEntryFromJsonString(it)
            }
            .stateIn(
                coroutineScopeIo,
                started = SharingStarted.Eagerly,
                initialValue = mapWallpaperSystemPhotoStatusCacheEntryFromJsonString(sourceData.allCache.value),
            )

    override fun add(cacheEntry: WallpaperSystemPhotoStatusCacheEntry) {
        val existing = get(cacheEntry.id)
        if (existing != null && existing == cacheEntry) {
            Log.d("add: already exists: $existing")
            return
        }

        val updated = all.value.toMutableList().apply {
            if (existing != null) {
                remove(existing)
            }
            add(cacheEntry)
        }
        sourceDataStringCache.value =
            mapWallpaperSystemPhotoStatusCacheEntryToJsonString(updated)
    }

    override fun remove(id: Id) {
        val updated = all.value.toMutableList().apply {
            val existingEntry = find { it.id == id }
            if (existingEntry != null) {
                remove(existingEntry)
            }
        }
        sourceDataStringCache.value = mapWallpaperSystemPhotoStatusCacheEntryToJsonString(updated)
    }

    override fun get(id: Id): WallpaperSystemPhotoStatusCacheEntry? {
        return all.value.find { it.id == id }
    }
}