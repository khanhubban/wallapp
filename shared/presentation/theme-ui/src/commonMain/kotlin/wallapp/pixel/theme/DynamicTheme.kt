package wallapp.pixel.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import wallapp.graphics.Color
import wallapp.graphics.color.isLight
import wallapp.graphics.composeColor

@Composable
fun dynamicColorScheme(
    themeColors: ThemeColors,
): ColorScheme {
    return dynamicColorSchemeAndDarkStatusBarIcons(themeColors).first
}

@Composable
fun dynamicColorSchemeAndDarkStatusBarIcons(
    themeColors: ThemeColors,
): Pair<ColorScheme, Boolean> {
    return dynamicColorSchemeAndDarkStatusBarIcons(
        background = themeColors.background,
        onBackground = themeColors.onBackground?.color,
        surface = themeColors.surface,
        onSurface = themeColors.onSurface?.color,
        surfaceVariant = themeColors.surfaceVariant,
        onSurfaceVariant = themeColors.onSurfaceVariant,
        primary = themeColors.primary,
        onPrimary = themeColors.onPrimary?.color,
        secondary = themeColors.secondary,
        onSecondary = themeColors.onSecondary?.color,
        tertiary = themeColors.tertiary?.color,
        onTertiary = themeColors.onTertiary?.color,
        scrim = themeColors.scrim?.color,
        outline = themeColors.outline,
    )
}

@Composable
fun dynamicColorSchemeAndDarkStatusBarIcons(
    background: Color,
    onBackground: Color? = null,
    surface: Color,
    onSurface: Color? = null,
    surfaceVariant: Color? = null,
    onSurfaceVariant: Color? = null,
    primary: Color,
    onPrimary: Color? = null,
    secondary: Color,
    onSecondary: Color? = null,
    tertiary: Color? = null,
    onTertiary: Color? = null,
    scrim: Color? = null,
    outline: Color? = null,
): Pair<ColorScheme, Boolean> {
    val isLight = background.isLight()
    val (baseColorScheme, darkStatusBarIcons) = if (isLight) {
        lightColorScheme() to true
    } else {
        darkColorScheme() to false
    }

    return baseColorScheme.copy(
        background = background.composeColor,
        onBackground = onBackground?.composeColor ?: baseColorScheme.onBackground,
        surface = surface.composeColor,
        onSurface = onSurface?.composeColor ?: baseColorScheme.onSurface,
        surfaceVariant = surfaceVariant?.composeColor ?: baseColorScheme.surfaceVariant,
        onSurfaceVariant = onSurfaceVariant?.composeColor ?: baseColorScheme.onSurfaceVariant,
        primary = primary.composeColor,
        onPrimary = onPrimary?.composeColor ?: baseColorScheme.onPrimary,
        secondary = secondary.composeColor,
        onSecondary = onSecondary?.composeColor ?: baseColorScheme.onSecondary,
        tertiary = tertiary?.composeColor ?: baseColorScheme.tertiary,
        onTertiary = onTertiary?.composeColor ?: baseColorScheme.onTertiary,
        scrim = scrim?.composeColor ?: baseColorScheme.scrim,
        outline = outline?.composeColor ?: baseColorScheme.outline,
    ) to darkStatusBarIcons
}
