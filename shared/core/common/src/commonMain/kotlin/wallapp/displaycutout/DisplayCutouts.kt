package wallapp.displaycutout

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.type.Side
import wallapp.utils.WindowDimens


@Serializable
data class DisplayCutouts(
    val topCutout: DisplayCutout?,
    val leftCutout: DisplayCutout?,
    val rightCutout: DisplayCutout?,
) {

    val preferredCutout: DisplayCutout?
        get() = when {
            topCutout != null -> topCutout
            leftCutout != null -> leftCutout
            rightCutout != null -> rightCutout
            else -> null
        }

    fun getPreferredCutoutSide(windowDimens: WindowDimens): Side? = when {
        topCutout != null -> {
            val xFraction = topCutout.visualCenter.x / windowDimens.minDisplayDimension
            when {
                xFraction <= .2 -> { Side.Left }
                xFraction >= .8f -> { Side.Right }
                else -> { null }
            }
        }
        leftCutout != null -> Side.Left
        rightCutout != null -> Side.Right
        else -> null
    }

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): DisplayCutouts? {
            if (exportString.isEmpty()) return null

            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}


val DisplayCutouts?.hasAny: Boolean
    get() {
        if (this == null) return false

        return topCutout != null || leftCutout != null || rightCutout != null
    }