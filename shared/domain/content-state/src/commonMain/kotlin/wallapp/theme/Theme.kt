package wallapp.theme

import androidx.compose.runtime.Immutable
import wallapp.pixel.theme.ThemeColors

/**
 * The theme that displays in the app.
 */
@Immutable
data class Theme(
    val isDark: Boolean,
    val themeColors: ThemeColors,
) {
    val isLight: Boolean
        get() = !isDark

    val label: String
        get() = if (isDark) { "Dark" } else { "Light" }
}
