package wallapp.pixel.separator

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewState

@Immutable
class SeparatorViewState(
    val width: Dp = 36.dp,
    val height: Dp = 6.dp,
) : ViewState