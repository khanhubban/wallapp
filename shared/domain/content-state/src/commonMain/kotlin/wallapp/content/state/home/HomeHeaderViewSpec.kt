package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec
import wallapp.unit.Padding

@Immutable
data class HomeHeaderViewSpec(
    val height: Dp,
    val profileVerticalPadding: Dp,
    val separatorHeight: Dp,
    val separatorPadding: Padding,
    val filterHeight: Dp,
) : ViewSpec