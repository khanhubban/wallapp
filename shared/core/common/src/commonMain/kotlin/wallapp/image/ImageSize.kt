package wallapp.image

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import wallapp.unit.Size
import wallapp.unit.pxOrElse
import wallapp.util.simpleClassName

/**
 * Represents the size of an image in pixels.
 */
@Serializable
@Immutable
data class ImageSize(
    val width: Int,
    val height: Int,
) {
    val size: Size by lazy { Size(width, height) }
}

val Size.imageSize: ImageSize get() = ImageSize(
    width.pxOrElse { throw IllegalArgumentException("Unsupported type - ${width.simpleClassName}") }!!,
    height.pxOrElse { throw IllegalArgumentException("Unsupported type - ${width.simpleClassName}") }!!,
)