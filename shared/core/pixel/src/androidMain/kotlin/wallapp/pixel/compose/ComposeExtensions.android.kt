package wallapp.pixel.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import wallapp.system.window.WindowFrame
import androidx.compose.runtime.collectAsState as collectAsStateRuntime

@Composable
actual fun <T> StateFlow<T>.collectAsState(useLifecycleIfAvailable: Boolean): State<T> {
    return if (useLifecycleIfAvailable) {
        // This currently is a workaround for the following issue - https://issuetracker.google.com/issues/336842920
        collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)
    } else {
        collectAsStateRuntime()
    }
}

actual fun Modifier.statusBarsPadding(windowFrame: WindowFrame): Modifier =
    this.padding(top = windowFrame.statusBarHeight)

actual fun Modifier.navigationBarsPadding(windowFrame: WindowFrame) =
    this.padding(bottom = windowFrame.navigationBarHeight)