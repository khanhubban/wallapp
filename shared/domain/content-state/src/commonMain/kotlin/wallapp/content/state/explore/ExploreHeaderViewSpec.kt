package wallapp.content.state.explore

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class ExploreHeaderViewSpec(
    val height: Dp,
    val offsetDueToHighlightCarousel: Dp,
) : ViewSpec
