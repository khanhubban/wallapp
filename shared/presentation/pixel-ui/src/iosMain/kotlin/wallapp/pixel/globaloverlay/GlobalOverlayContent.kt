package wallapp.pixel.globaloverlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render

@Composable
actual fun GlobalOverlayContent(
    render: Render,
    viewState: GlobalOverlayViewState.Data,
    modifier: Modifier,
) {
    // Handled in iOS natively
}