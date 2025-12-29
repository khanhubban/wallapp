package wallapp.pixel.view

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop

@Immutable
@SealedInterop.Enabled
sealed class ViewAlignment {

    @Immutable
    data object Center : ViewAlignment()

    @Immutable
    data class ParallaxVertical(val maxScale: Float) : ViewAlignment()
}
