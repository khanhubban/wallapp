package wallapp.pixel.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow
import wallapp.system.window.WindowFrame
import androidx.compose.runtime.collectAsState as collectAsStateRuntime

@Composable
actual fun <T> StateFlow<T>.collectAsState(useLifecycleIfAvailable: Boolean): State<T> {
    return collectAsStateRuntime()
}

actual fun Modifier.statusBarsPadding(windowFrame: WindowFrame): Modifier =
    this.padding(top = windowFrame.statusBarHeight)

actual fun Modifier.navigationBarsPadding(windowFrame: WindowFrame): Modifier =
    this.padding(bottom = windowFrame.navigationBarHeight)