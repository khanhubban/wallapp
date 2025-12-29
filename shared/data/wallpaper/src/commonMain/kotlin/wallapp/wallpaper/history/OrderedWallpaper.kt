package wallapp.wallpaper.history

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.content.model.Id.RemixId


@Serializable
data class OrderedWallpaper(
    val remixId: RemixId,
    val order: Int,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): OrderedWallpaper {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}