package wallapp.displaycutout

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.math.geometry.PointF
import wallapp.math.geometry.Rect


@Serializable
data class DisplayCutout(
    val bounds: Rect,
    val visualCenter: PointF,
) {
    constructor(bounds: Rect): this(bounds, bounds.center)

    val inferredDisplayCutoutType: DisplayCutoutType by lazy {
        bounds.inferDisplayCutoutType()
    }

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): DisplayCutout {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}