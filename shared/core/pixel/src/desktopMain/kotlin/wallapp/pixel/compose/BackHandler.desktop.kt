package wallapp.pixel.compose

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) = BackHandlerNoOp(enabled, onBack)