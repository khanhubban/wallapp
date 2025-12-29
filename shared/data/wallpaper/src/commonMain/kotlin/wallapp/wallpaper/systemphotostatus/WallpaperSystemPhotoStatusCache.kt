package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id

interface WallpaperSystemPhotoStatusCache {

    fun get(id: Id): WallpaperSystemPhotoStatusCacheEntry?
    fun add(cacheEntry: WallpaperSystemPhotoStatusCacheEntry)
    fun remove(id: Id)

    val all: Flow<List<WallpaperSystemPhotoStatusCacheEntry>>
}