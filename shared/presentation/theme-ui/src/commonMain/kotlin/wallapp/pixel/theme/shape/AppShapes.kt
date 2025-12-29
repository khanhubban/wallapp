package wallapp.pixel.theme.shape

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import wallapp.pixel.shape.ShapeStyle

object AppShapes {

    val CutCorner = Shapes(
        extraSmall = CutCornerShape(
            topStart = 8.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        small = CutCornerShape(
            topStart = 12.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        medium = CutCornerShape(
            topStart = 16.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        large = CutCornerShape(
            topStart = 20.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        extraLarge = CutCornerShape(
            topStart = 24.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
    )

    val CutCorners = Shapes(
        extraSmall = CutCornerShape(
            topStart = 8.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 8.dp
        ),
        small = CutCornerShape(
            topStart = 12.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 12.dp
        ),
        medium = CutCornerShape(
            topStart = 16.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 16.dp
        ),
        large = CutCornerShape(
            topStart = 20.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 20.dp
        ),
        extraLarge = CutCornerShape(
            topStart = 24.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 24.dp
        ),
    )

    val RoundedCornerBottomStart = Shapes(
        extraSmall = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 4.dp,
            bottomEnd = 0.dp,
        ),
        small = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 8.dp,
            bottomEnd = 0.dp,
        ),
        medium = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 0.dp,
        ),
        large = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 0.dp,
        ),
        extraLarge = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 0.dp,
        )
    )

    val RoundedCornersBottom = Shapes(
        extraSmall = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp,
        ),
        small = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 8.dp,
            bottomEnd = 8.dp,
        ),
        medium = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp,
        ),
        large = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp,
        ),
        extraLarge = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp,
        )
    )

    val RoundedCornersTop = Shapes(
        extraSmall = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        ),
        small = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        ),
        medium = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        ),
        large = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        ),
        extraLarge = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        )
    )


    val RoundedCorners = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp)
    )

    val RoundedCornersTopEndBottomStart = Shapes(
        extraSmall = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 4.dp,
            bottomStart = 4.dp,
            bottomEnd = 0.dp,
        ),
        small = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 8.dp,
            bottomStart = 8.dp,
            bottomEnd = 0.dp,
        ),
        medium = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 0.dp,
        ),
        large = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 0.dp,
        ),
        extraLarge = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 24.dp,
            bottomStart = 24.dp,
            bottomEnd = 0.dp,
        )
    )

    val Square = Shapes(
        extraSmall = CutCornerShape(0.dp),
        small = CutCornerShape(0.dp),
        medium = CutCornerShape(0.dp),
        large = CutCornerShape(0.dp),
        extraLarge = CutCornerShape(0.dp),
    )

    val Circle = Shapes(
        extraSmall = RoundedCornerShape(50),
        small = RoundedCornerShape(50),
        medium = RoundedCornerShape(50),
        large = RoundedCornerShape(50),
        extraLarge = RoundedCornerShape(50),
    )

    fun getShapes(shapeStyle: ShapeStyle): Shapes {
        return when (shapeStyle) {
            ShapeStyle.Circle -> Circle
            ShapeStyle.CutCorner -> CutCorner
            ShapeStyle.CutCorners -> CutCorners
            ShapeStyle.Rectangle -> Square
            ShapeStyle.RoundedCornerBottomStart -> RoundedCornerBottomStart
            ShapeStyle.RoundedCorners -> RoundedCorners
            ShapeStyle.RoundedCornersBottom -> RoundedCornersBottom
            ShapeStyle.RoundedCornersTop -> RoundedCornersTop
            ShapeStyle.RoundedCornersTopEndBottomStart -> RoundedCornersTopEndBottomStart
        }
    }
}