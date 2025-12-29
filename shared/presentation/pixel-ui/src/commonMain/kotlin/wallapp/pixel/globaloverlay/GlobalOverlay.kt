package wallapp.pixel.globaloverlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render

@Composable
fun GlobalOverlay(
    render: Render,
    viewState: GlobalOverlayViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is GlobalOverlayViewState.Data -> GlobalOverlayContent(render, viewState, modifier)
        is GlobalOverlayViewState.None -> { }
    }
}

@Composable
expect fun GlobalOverlayContent(
    render: Render,
    viewState: GlobalOverlayViewState.Data,
    modifier: Modifier = Modifier,
)