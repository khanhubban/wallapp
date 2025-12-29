package wallapp.theme

import wallapp.graphics.Colors
import wallapp.pixel.theme.ThemeColors
import wallapp.pixel.util.ColorOptional

object Themes {

    const val LightSurfaceVariantAlpha = .97f

    val Light = Theme(
        isDark = false,
        themeColors = ThemeColors(
            background = Colors.GrayLight,
            surface = Colors.White,
            surfaceVariant = Colors.White.copy(alpha = LightSurfaceVariantAlpha),
            onSurfaceVariant = Colors.Gray,
            primary = Colors.Black,
            onPrimary = ColorOptional(Colors.White),
            secondary = Colors.Accent,
            onSecondary = ColorOptional(Colors.White),
            tertiary = ColorOptional(Colors.GrayHighlight),
            onTertiary = ColorOptional(Colors.Black),
            scrim = ColorOptional(Colors.ScrimLightTheme),
            outline = Colors.OutlineLightTheme,
        ),
    )

    val LightAlt = Light.copy(
        themeColors = Light.themeColors.copy(
            background = Colors.White,
            surface = Colors.GrayLight,
            surfaceVariant = Colors.GrayLight.copy(alpha = LightSurfaceVariantAlpha),
        ),
    )

    const val DarkSurfaceVariantAlpha = .97f

    val Dark = Theme(
        isDark = true,
        themeColors = ThemeColors(
            background = Colors.BlackMatte,
            surface = Colors.BlackMatteAlt,
            surfaceVariant = Colors.BlackMatteAlt.copy(alpha = DarkSurfaceVariantAlpha),
            onSurfaceVariant = Colors.Gray,
            primary = Colors.White,
            onPrimary = ColorOptional(Colors.Black),
            secondary = Colors.Accent,
            onSecondary = ColorOptional(Colors.White),
            tertiary = ColorOptional(Colors.Gray),
            onTertiary = ColorOptional(Colors.Black),
            scrim = ColorOptional(Colors.ScrimDarkTheme),
            outline = Colors.OutlineDarkTheme,
        ),
    )

    val DarkAlt = Dark.copy(
        themeColors = Dark.themeColors.copy(
            background = Colors.BlackMatte,
            surface = Colors.Black,
            surfaceVariant = Colors.Black.copy(alpha = DarkSurfaceVariantAlpha),
        ),
    )

}