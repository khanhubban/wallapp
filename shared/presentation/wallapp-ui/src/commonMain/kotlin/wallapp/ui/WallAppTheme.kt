package wallapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import wallapp.pixel.compose.collectAsState
import wallapp.pixel.render.Render
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.dynamicColorScheme
import wallapp.theme.Theme

@Composable
fun WallAppTheme(
    render: Render,
    screenControllers: ScreenControllers,
    theme: Theme,
    typography: Typography,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val systemBarColors by render.windowManager.systemBarColors.collectAsState()
    screenControllers.screenManager.isSystemInDarkTheme = isSystemInDarkTheme

    AppTheme(
        render = render,
        systemBarColors,
        colorScheme = dynamicColorScheme(themeColors = theme.themeColors),
        typography = typography,
        content = content,
    )
}
