package wallapp.unit

import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.unit.Height.HeightDp

@SealedInterop.Enabled
sealed class Height {
    data object HeightUndefined : Height()

    data class HeightDp(val dp: Dp) : Height()

    data class HeightFillMax(val fraction: Float = 1f) : Height()
}


val Dp.height: Height
    get() = HeightDp(this)