package wallapp.pixel.theme

import androidx.compose.material3.Shapes
import wallapp.pixel.shape.ShapeStyle
import wallapp.pixel.theme.shape.AppShapes


//@get:Composable
//val Theme.colorScheme: ColorScheme
//    get() = when (this) {
////        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
////            val context = LocalContext.current
////            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
////        }
//        Theme.Dark -> Colors.DarkColors
//        Theme.Light -> Colors.LightColors
//        else -> {
//            throw IllegalArgumentException("Unsupported theme: $this")
//        }
//    }

val ShapeStyle.shapes: Shapes
    get() = when (this) {
        ShapeStyle.Circle -> throw IllegalStateException("Circle is not supported")
        ShapeStyle.CutCorner -> AppShapes.CutCorner
        ShapeStyle.CutCorners -> AppShapes.CutCorners
        ShapeStyle.Rectangle -> AppShapes.Square
        ShapeStyle.RoundedCornerBottomStart -> AppShapes.RoundedCornerBottomStart
        ShapeStyle.RoundedCorners -> AppShapes.RoundedCorners
        ShapeStyle.RoundedCornersBottom -> AppShapes.RoundedCornersBottom
        ShapeStyle.RoundedCornersTop -> AppShapes.RoundedCornersTop
        ShapeStyle.RoundedCornersTopEndBottomStart -> AppShapes.RoundedCorners
    }
