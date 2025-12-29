package wallapp.math.geometry

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Rect(var left: Float = 0f, var top: Float = 0f, var right: Float = 0f, var bottom: Float = 0f) {

//    constructor(other: RectF) : this(other.left, other.top, other.right, other.bottom)

    constructor(other: Rect) : this(other.left, other.top, other.right, other.bottom)

    fun set(left: Float = this.left, top: Float = this.top,
            right: Float = this.right, bottom: Float = this.bottom) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    fun set(rect: Rect) {
        left = rect.left
        top = rect.top
        right = rect.right
        bottom = rect.bottom
    }

    fun offset(dx: Float, dy: Float) {
        left += dx
        top += dy
        right += dx
        bottom += dy
    }

    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    val aspectRatio: Float
        get() = width * 1f / height

    val centerX: Float
        get() = (left + right) * .5f

    val centerY: Float
        get() = (top + bottom) * .5f

    val center: PointF by lazy {
        PointF(centerX, centerY)
    }

    fun contains(point: PointF): Boolean {
        return point.x in left..right
                && point.y >= top
                && point.y <= bottom
    }

    override fun toString(): String {
//        return "left=%.3f, top=%.3f, right=%.3f, bottom=%.3f".format(
//            left, top, right, bottom
//        )
        return "left=${left}, top=${top}, right=${right}, bottom=${bottom}"
    }

    fun asExportString(): String = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {

        fun fromExportString(exportString: String): Rect {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}

fun Rect.normalize(width: Float, height: Float): Rect {
    return Rect(this).also {
        it.left = it.left / width
        it.right = it.right / width
        it.top = it.top / height
        it.bottom = it.bottom / height
    }
}

fun Rect.zoomOut(zoomScale: Float) {
    left -= width * zoomScale
    right += width * zoomScale
    top -= height * zoomScale
    bottom += height * zoomScale
}

fun Rect.mirrorX(referenceRect: Rect) {
    val oldRect = Rect(this)
    left = referenceRect.right - oldRect.right
    right = referenceRect.right - oldRect.left
}

val List<Rect>.bounds: Rect
    get() = Rect(
        left = map { it.left }.minOf { it },
        top = map { it.top }.minOf { it },
        right = map { it.right }.maxOf { it },
        bottom = map { it.bottom }.maxOf { it },
    )
