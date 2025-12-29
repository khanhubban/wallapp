package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec
import wallapp.unit.Padding

@Immutable
data class ProfileHeaderViewSpec(
    val padding: Padding,
    val statusBarHeight: Dp,
    val height: Dp,
    val titleHeight: Dp,
    val subtitleTopPadding: Dp,
    val subtitleHeight: Dp,
) : ViewSpec