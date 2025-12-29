package wallapp.unit

import androidx.compose.ui.Alignment as ComposeAlignment
import androidx.compose.ui.BiasAbsoluteAlignment as ComposeBiasAbsoluteAlignment
import androidx.compose.ui.BiasAlignment as ComposeBiasAlignment


val Alignment.composeAlignment: ComposeAlignment
    get() = when(this) {
        is BiasAlignment -> ComposeBiasAlignment(horizontalBias, verticalBias)
        is BiasAbsoluteAlignment -> ComposeBiasAbsoluteAlignment(horizontalBias, verticalBias)
        else -> {
            error("Unknown Alignment type: $this")
        }
    }
