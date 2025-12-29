package wallapp.pixel.globaloverlay

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.Flow

interface GlobalOverlayManager {
    fun show(viewState: GlobalOverlayViewState)
    fun hide()

    @FlowInterop.Enabled
    val globalOverlayViewState: Flow<GlobalOverlayViewState>
}