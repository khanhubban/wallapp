package wallapp.pixel.compose

import androidx.compose.runtime.Composable

@Composable
fun BackHandler(onBack: () -> Unit) {
    BackHandler(enabled = true, onBack = onBack)
}

@Composable
expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun BackHandlerNoOp(enabled: Boolean = true, onBack: () -> Unit) { }