package wallapp.pixel.globaloverlay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.log.Log

class GlobalOverlayManagerDefault : GlobalOverlayManager {

    private val _globalOverlayViewState =
        MutableStateFlow<GlobalOverlayViewState>(GlobalOverlayViewState.None)

    override fun show(viewState: GlobalOverlayViewState) {
        Log.d("[firebase] [GlobalOverlayManagerDefault] show: $viewState")
        _globalOverlayViewState.value = viewState
    }

    override fun hide() {
        Log.d("[firebase] [GlobalOverlayManagerDefault] hide")
        _globalOverlayViewState.value = GlobalOverlayViewState.None

    }

    override val globalOverlayViewState: Flow<GlobalOverlayViewState>
        get() = _globalOverlayViewState
}