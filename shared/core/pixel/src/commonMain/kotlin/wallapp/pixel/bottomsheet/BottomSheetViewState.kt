package wallapp.pixel.bottomsheet

import androidx.compose.runtime.Immutable
import wallapp.graphics.Color
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.view.ViewState

@Immutable
data class BottomSheetViewState(
    val screenViewState: ScreenViewState?,
    val scrimColor: Color?,
): ViewState
