package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class SearchInputViewSpec(
    val topPadding: Dp,
    val searchBarHeight: Dp,
    val searchFiltersTopPadding: Dp,
    val searchBarHorizontalPadding: Dp,
    val filterToggleHeight: Dp,
    val filtersHorizontalPadding: Dp = 32.dp,
) : ViewSpec
