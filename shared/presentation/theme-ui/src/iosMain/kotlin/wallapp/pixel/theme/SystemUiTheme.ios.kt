package wallapp.pixel.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import wallapp.log.Log
import wallapp.system.window.SystemBarColors

@Composable
actual fun SystemUiTheme(
    systemBarColors: SystemBarColors,
) {
    DisposableEffect(systemBarColors) {
        val darkStatusBarIcons = systemBarColors.darkStatusBarIcons
//        if (darkStatusBarIcons != null) {
//        }
        Log.d("SystemUiTheme: darkStatusBarIcons: $darkStatusBarIcons")

        onDispose { }
    }
}