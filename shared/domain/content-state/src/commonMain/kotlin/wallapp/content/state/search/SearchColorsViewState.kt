package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewState

@Immutable
data class SearchColorsViewState(
    val colors: List<SearchColorViewState>,
    val borderShape: ShapeSpec,
    val backgroundShape: ShapeSpec,
    val itemSize: Dp,
    val insideItemSize: Dp,
) : ViewState
