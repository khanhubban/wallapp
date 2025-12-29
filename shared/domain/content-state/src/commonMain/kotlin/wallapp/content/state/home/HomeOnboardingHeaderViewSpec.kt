package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class HomeOnboardingHeaderViewSpec(
    val height: Dp,
    val verticalPadding: Dp,
    val itemSpacing: Dp,
): ViewSpec
