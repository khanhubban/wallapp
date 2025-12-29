package wallapp.pixel.globaloverlay

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.image.Image
import wallapp.pixel.text.Text
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
@SealedInterop.Enabled
sealed class GlobalOverlayViewState : ViewState {

    @Immutable
    data object None : GlobalOverlayViewState()

    @Immutable
    data class Data(
        val scrimColor: ColorToken,
        val image: Image?,
        val imageSize: DpOptional? = DpOptional(80.dp),
        val message: Text?,
    ) : GlobalOverlayViewState()
}

fun GlobalOverlayViewStateDataScrim(): GlobalOverlayViewState.Data {
    return GlobalOverlayViewState.Data(
        scrimColor = ColorToken.ThemeScrim,
        image = null,
        imageSize = null,
        message = null,
    )
}
