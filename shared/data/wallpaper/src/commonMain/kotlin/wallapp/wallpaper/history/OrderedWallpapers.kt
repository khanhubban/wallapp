package wallapp.wallpaper.history

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OrderedWallpapers(
    val wallpapers: Set<OrderedWallpaper>,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): OrderedWallpapers? {
            if (exportString.isEmpty()) return null
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}
