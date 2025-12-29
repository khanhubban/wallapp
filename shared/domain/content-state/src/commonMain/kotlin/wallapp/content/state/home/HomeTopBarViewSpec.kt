package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class HomeTopBarViewSpec(
    val height: Dp,
    val tabContainerHeight: Dp,
    val profileVerticalPadding: Dp,
) : ViewSpec
