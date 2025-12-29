package wallapp.content.state.showcase.typeface

import androidx.compose.runtime.Immutable
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.toolbar.ToolbarViewState

@Immutable
data class ShowcaseTypefaceViewState(
    val toolbar: ToolbarViewState,
) : ScreenViewState
