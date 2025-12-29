package wallapp.pixel.theme.shape

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection


abstract class AppCornerBasedShape(
    val topStart: CornerSize,
    val topEnd: CornerSize,
    val bottomEnd: CornerSize,
    val bottomStart: CornerSize,
    val defaultCornerRadius : CornerSize,
) : Shape {

    final override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        var topStart = topStart.toPx(size, density)
        var topEnd = topEnd.toPx(size, density)
        var bottomEnd = bottomEnd.toPx(size, density)
        var bottomStart = bottomStart.toPx(size, density)
        val defaultCornerRadius = defaultCornerRadius.toPx(size, density)
        val minDimension = size.minDimension
        if (topStart + bottomStart > minDimension) {
            val scale = minDimension / (topStart + bottomStart)
            topStart *= scale
            bottomStart *= scale
        }
        if (topEnd + bottomEnd > minDimension) {
            val scale = minDimension / (topEnd + bottomEnd)
            topEnd *= scale
            bottomEnd *= scale
        }
        require(topStart >= 0.0f && topEnd >= 0.0f && bottomEnd >= 0.0f && bottomStart >= 0.0f) {
            "Corner size in Px can't be negative(topStart = $topStart, topEnd = $topEnd, " +
                    "bottomEnd = $bottomEnd, bottomStart = $bottomStart)!"
        }
        return createOutline(
            size = size,
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            defaultCornerRadius = defaultCornerRadius,
            layoutDirection = layoutDirection
        )
    }


    abstract fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        defaultCornerRadius : Float,
        layoutDirection: LayoutDirection
    ): Outline

    /**
     * Creates a copy of this Shape with a new corner sizes.
     *
     * @param topStart a size of the top start corner
     * @param topEnd a size of the top end corner
     * @param bottomEnd a size of the bottom end corner
     * @param bottomStart a size of the bottom start corner
     */
    abstract fun copy(
        topStart: CornerSize = this.topStart,
        topEnd: CornerSize = this.topEnd,
        bottomEnd: CornerSize = this.bottomEnd,
        bottomStart: CornerSize = this.bottomStart,
        defaultCornerRadius: CornerSize = this.defaultCornerRadius
    ): AppCornerBasedShape

    /**
     * Creates a copy of this Shape with a new corner size.
     * @param all a size to apply for all four corners
     */
    fun copy(all: CornerSize): AppCornerBasedShape = copy(all, all, all, all, CornerSize(0))
}
