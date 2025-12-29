package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.shape.ShapeSpec

@Immutable
data class SearchResultsHeaderViewSpec(
    val barTopPadding: Dp,
    val headerHeight: Dp,
    val searchBarExternalHorizontalPadding: Dp,
    val searchBarInternalHorizontalPadding: Dp,
    val searchBarHeight: Dp,
    val searchBarShapeSpec: ShapeSpec,
)