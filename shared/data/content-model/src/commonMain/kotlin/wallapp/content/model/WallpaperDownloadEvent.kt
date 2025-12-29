package wallapp.content.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.content.model.Id.RemixId

@Serializable
data class WallpaperDownloadEvent(
    val id: String,
    val times: MutableList<Long>,
) {
    val wallpaperId: RemixId by lazy { Id.fromExportShortString(id) as RemixId }
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    companion object {
        fun fromExportString(exportString: String): WallpaperDownloadEvent {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}