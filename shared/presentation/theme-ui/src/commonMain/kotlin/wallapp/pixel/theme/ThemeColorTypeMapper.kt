package wallapp.pixel.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import wallapp.graphics.composeColor
import wallapp.theme.ColorToken
import kotlin.jvm.JvmName

object ThemeColorTypeMapper {

    @Composable
    fun map(colorToken: ColorToken): Color {
        val colorScheme = MaterialTheme.colorScheme
        return when (colorToken) {
            ColorToken.ThemeBackground -> colorScheme.background
            ColorToken.ThemeOnBackground -> colorScheme.onBackground
            ColorToken.ThemeSurface -> colorScheme.surface
            ColorToken.ThemeOnSurface -> colorScheme.onSurface
            ColorToken.ThemeSurfaceVariant -> colorScheme.surfaceVariant
            ColorToken.ThemeOnSurfaceVariant -> colorScheme.onSurfaceVariant
            ColorToken.ThemePrimary -> colorScheme.primary
            ColorToken.ThemeOnPrimary -> colorScheme.onPrimary
            ColorToken.ThemeSecondary -> colorScheme.secondary
            ColorToken.ThemeOnSecondary -> colorScheme.onSecondary
            ColorToken.ThemeTertiary -> colorScheme.tertiary
            ColorToken.ThemeOnTertiary -> colorScheme.onTertiary
            ColorToken.LocalContent -> LocalContentColor.current
            ColorToken.Transparent -> Color.Transparent
            ColorToken.ThemeScrim -> colorScheme.scrim
            is ColorToken.Custom -> colorToken.color.composeColor
        }
    }

    @JvmName("mapThemeColorTokenNullable")
    @Composable
    fun map(colorToken: ColorToken?): Color? {
        if (colorToken == null) {
            return null
        }
        return map(colorToken)
    }
}