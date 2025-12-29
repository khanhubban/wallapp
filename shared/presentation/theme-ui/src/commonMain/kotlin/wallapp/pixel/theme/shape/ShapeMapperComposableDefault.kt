package wallapp.pixel.theme.shape

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import wallapp.pixel.shape.ShapeMapperComposable
import wallapp.pixel.shape.ShapeSize
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.ShapeStyle

object ShapeMapperComposableDefault : ShapeMapperComposable {

    @Composable
    override fun map(shapeSize: ShapeSize): Shape {
        val shapes = MaterialTheme.shapes
        return map(shapes, shapeSize)
    }

    @Composable
    fun map(shapes: Shapes, shapeSize: ShapeSize): Shape {
        return when (shapeSize) {
            ShapeSize.ExtraSmall -> shapes.extraSmall
            ShapeSize.Small -> shapes.small
            ShapeSize.Medium -> shapes.medium
            ShapeSize.Large -> shapes.large
            ShapeSize.ExtraLarge -> shapes.extraLarge
        }
    }

    @Composable
    fun ShapeStyle.shape(): Shape =
        when (this) {
            ShapeStyle.Circle -> RoundedCornerShape(50)
            ShapeStyle.CutCorner -> CutCornerShape(topStart = 12.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
            ShapeStyle.CutCorners -> CutCornerShape(topStart = 12.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 12.dp)
            ShapeStyle.Rectangle -> CutCornerShape(0.dp)
            ShapeStyle.RoundedCornerBottomStart -> RoundedCornerShape(bottomStart = 16.dp)
            ShapeStyle.RoundedCorners -> RoundedCornerShape(16.dp)
            ShapeStyle.RoundedCornersBottom -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ShapeStyle.RoundedCornersTop -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ShapeStyle.RoundedCornersTopEndBottomStart -> RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 0.dp)
        }

    @Composable
    override fun map(shapeSpec: ShapeSpec): Shape? {
        when (shapeSpec) {
            is ShapeSpec.Single -> return map(shapeSpec)
            is ShapeSpec.Multiple -> {
                throw IllegalArgumentException("Mapping multiple shape specs are not supported")
            }
        }
    }

    @Composable
    fun map(shapeSpec: ShapeSpec.Single): Shape? {
        val style = shapeSpec.shapeStyle
        val size = shapeSpec.shapeSize

        if (style == null && size == null) {
            return null
        }

        return if (style == null) {
            map(size!!)
        } else {
            require(size != null)
            val shapes = AppShapes.getShapes(style)
            map(shapes, size)
        }
    }
}