package wallapp.pixel.animation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewSpec
import wallapp.unit.Padding

@Immutable
data class AnimatedViewSpec(
    val heightMin: DpOptional? = null,
    val heightMax: DpOptional? = null,
    val widthMin: DpOptional? = null,
    val widthMax: DpOptional? = null,
    val paddingMin: Padding? = null,
    val paddingMax: Padding? = null,
    val alphaMin: Float? = null,
    val alphaMax: Float? = null,
    val shapeSpecMin: ShapeSpec? = null,
    val shapeSpecMax: ShapeSpec? = null,
    val alignmentWhenCollapsed: Alignment? = null,
    val alignmentWhenExpanded: Alignment? = null,
) : ViewSpec {

    constructor(
        alpha: Float? = null,
        height: Dp? = null,
        width: Dp? = null,
        padding: Padding? = null,
        shapeSpec: ShapeSpec? = null,
    ) : this(
        alphaMin = alpha,
        alphaMax = alpha,
        heightMin = height?.let { DpOptional(it) },
        heightMax = height?.let { DpOptional(it) },
        widthMin = width?.let { DpOptional(it) },
        widthMax = width?.let { DpOptional(it) },
        paddingMin = padding,
        paddingMax = padding,
        shapeSpecMin = shapeSpec,
        shapeSpecMax = shapeSpec,
    )

}
