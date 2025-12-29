package wallapp.math.geometry

import androidx.compose.ui.unit.Dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Point(
    val x: Int,
    val y: Int,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): PointF {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}

@Serializable
data class PointF(
    val x: Float,
    val y: Float,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): PointF {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}

@Serializable
data class PointD(
    val x: Double,
    val y: Double,
) {
    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        fun fromExportString(exportString: String): PointF {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}

data class PointDp(
    val x: Dp,
    val y: Dp,
)

val Point.pointF: PointF
    get() = PointF(x.toFloat(), y.toFloat())
val Point.pointD: PointD
    get() = PointD(x.toDouble(), y.toDouble())

val PointF.point: Point
    get() = Point(x.toInt(), y.toInt())
val PointF.pointD: PointD
    get() = PointD(x.toDouble(), y.toDouble())

val PointD.point: Point
    get() = Point(x.toInt(), y.toInt())
val PointD.pointF: PointF
    get() = PointF(x.toFloat(), y.toFloat())
