package wallapp.pixel.globaloverlay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object GlobalOverlayManagerNoOp : GlobalOverlayManager {
    override fun show(viewState: GlobalOverlayViewState) {
        /*No Op*/
    }

    override fun hide() {
        /*No Op*/
    }

    override val globalOverlayViewState: Flow<GlobalOverlayViewState>
        get() = flow {
            GlobalOverlayViewState.None
        }
}