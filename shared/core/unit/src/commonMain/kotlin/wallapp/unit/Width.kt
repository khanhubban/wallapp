package wallapp.unit

import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.unit.Width.WidthDp


@SealedInterop.Enabled
sealed class Width {
    data object WidthUndefined : Width()

    data class WidthDp(val dp: Dp) : Width()

    data class WidthFillMax(val fraction: Float = 1f) : Width()
}


val Dp.width: Width
    get() = WidthDp(this)