package wallapp.pixel.image

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewAlignment
import wallapp.pixel.view.ViewContentScale
import wallapp.pixel.view.ViewSpec

data class ImageViewSpec(
    val width: Dp,
    val height: Dp,
    val shapeSpec: ShapeSpec?,
    val contentScale: ViewContentScale = ViewContentScale.Crop,
    val alignment: ViewAlignment = ViewAlignment.Center,
) : ViewSpec {

    constructor(size: Dp, shapeSpec: ShapeSpec? = null) : this(
        width = size,
        height = size,
        shapeSpec = shapeSpec,
        contentScale = ViewContentScale.Fit,
    )

    constructor(
        other: ImageViewSpec,
        width: Dp = other.width,
        height: Dp = other.height,
        shapeSpec: ShapeSpec? = other.shapeSpec,
        contentScale: ViewContentScale = other.contentScale,
        alignment: ViewAlignment = other.alignment,
    ) : this(
        width = width,
        height = height,
        shapeSpec = shapeSpec,
        contentScale = contentScale,
        alignment = alignment,
    )

    val size: Dp
        get() {
            require(width == height) { "Width and height must be equal, $width != $height" }
            return width
        }

    companion object {
        val Preset = ImageViewSpec(
            width = 100.dp,
            height = 100.dp,
            shapeSpec = null,
        )
    }
}
