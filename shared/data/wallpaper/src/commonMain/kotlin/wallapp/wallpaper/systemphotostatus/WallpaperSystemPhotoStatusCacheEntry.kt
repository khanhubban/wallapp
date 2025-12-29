package wallapp.wallpaper.systemphotostatus

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wallapp.content.model.Id
import wallapp.system.photo.status.SystemPhotoStatus

@Serializable
data class WallpaperSystemPhotoStatusCacheEntry(
    val id: Id,
    val status: SystemPhotoStatus,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun from(string: String): WallpaperSystemPhotoStatusCacheEntry {
            return Json.decodeFromString(kotlinx.serialization.serializer(), string)
        }
    }
}

fun mapWallpaperSystemPhotoStatusCacheEntryToJsonString(
    items: List<WallpaperSystemPhotoStatusCacheEntry>,
): String {
    return Json.encodeToString(ListSerializer(WallpaperSystemPhotoStatusCacheEntry.serializer()), items)
}

fun mapWallpaperSystemPhotoStatusCacheEntryFromJsonString(string: String):
        List<WallpaperSystemPhotoStatusCacheEntry> {
    if (string.isEmpty()) return emptyList()
    return Json.decodeFromString(ListSerializer(WallpaperSystemPhotoStatusCacheEntry.serializer()), string)
}