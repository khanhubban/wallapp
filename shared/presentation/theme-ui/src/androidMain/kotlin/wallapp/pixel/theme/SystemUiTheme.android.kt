package wallapp.pixel.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import wallapp.graphics.composeColor
import wallapp.system.window.SystemBarColors

@Composable
actual fun SystemUiTheme(
    systemBarColors: SystemBarColors,
) {
    val systemUiController: SystemUiController = rememberSystemUiController()
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(systemUiController, systemBarColors) {
            val navigationColor = Color.Transparent

            val targetStatusBarColor = systemBarColors.statusBarColor.composeColor
            val darkStatusBarIcons = systemBarColors.darkStatusBarIcons
                    ?: (targetStatusBarColor.luminance() > 0.5f)

            systemUiController.setStatusBarColor(navigationColor, darkStatusBarIcons)

//            val darkSystemIcons = !isSystemInDarkTheme()
            systemUiController.setNavigationBarColor(navigationColor)
            onDispose { }
        }
    }
}