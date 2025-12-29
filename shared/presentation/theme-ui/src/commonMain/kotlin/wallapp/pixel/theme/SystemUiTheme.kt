package wallapp.pixel.theme

import androidx.compose.runtime.Composable
import wallapp.system.window.SystemBarColors

@Composable
expect fun SystemUiTheme(
    systemBarColors: SystemBarColors,
)