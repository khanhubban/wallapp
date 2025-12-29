package wallapp.pixel.compose

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler as BackHandlerActivity

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandlerActivity(enabled, onBack)
}