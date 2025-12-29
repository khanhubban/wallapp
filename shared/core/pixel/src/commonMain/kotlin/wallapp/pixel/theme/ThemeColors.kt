package wallapp.pixel.theme

import androidx.compose.runtime.Immutable
import wallapp.graphics.Color
import wallapp.graphics.color.getContrastingColor
import wallapp.pixel.util.ColorOptional

@Immutable
data class ThemeColors(
    val background: Color,
    val onBackground: ColorOptional? = ColorOptional(background.getContrastingColor()),
    val surface: Color,
    val onSurface: ColorOptional? = ColorOptional(surface.getContrastingColor()),
    val surfaceVariant: Color,
    val onSurfaceVariant: Color = surfaceVariant.getContrastingColor(),
    val primary: Color,
    val onPrimary: ColorOptional? = ColorOptional(primary.getContrastingColor()),
    val secondary: Color,
    val onSecondary: ColorOptional? = ColorOptional(secondary.getContrastingColor()),
    val tertiary: ColorOptional? = null,
    val onTertiary: ColorOptional? = tertiary?.color?.getContrastingColor()?.let { ColorOptional(it) },
    val scrim: ColorOptional? = null,
    val outline: Color,
) {

    companion object {

        val Preset = ThemeColors(
            background = Color.White,
            onBackground = ColorOptional(Color.Black),
            surface = Color.LightGray,
            surfaceVariant = Color.White.copy(alpha = .93f),
            primary = Color.Black,
            onPrimary = ColorOptional(Color.White),
            secondary = Color.Orange,
            tertiary = ColorOptional(Color.Gray),
            onTertiary = ColorOptional(Color.Black),
            outline = Color.Gray,
        )
    }
}