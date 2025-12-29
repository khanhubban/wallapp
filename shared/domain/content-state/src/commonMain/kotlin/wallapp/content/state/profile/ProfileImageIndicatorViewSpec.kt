package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class ProfileImageIndicatorViewSpec(
    val size: Dp,
    val borderSize: Dp,
)