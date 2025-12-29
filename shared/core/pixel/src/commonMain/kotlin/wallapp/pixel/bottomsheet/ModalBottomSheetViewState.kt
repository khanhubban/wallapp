package wallapp.pixel.bottomsheet

import androidx.compose.runtime.Immutable
import wallapp.graphics.Color
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewState

@Immutable
data class ModalBottomSheetViewState(
    val screenViewState: ScreenViewState,
    val scrimColor: Color?,
    val shapeSpec: ShapeSpec,
    /**
     * If true, the bottom sheet will be hidden. This API is pretty ugly, but works for now.
     */
    val requestHideState: Boolean,
    val onDismissed: () -> Unit,
): ViewState
