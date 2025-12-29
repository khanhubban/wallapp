package wallapp.unit

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
data class Padding(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
) {
    constructor(all: Dp) : this(all, all, all, all)

    constructor(horizontal: Dp = 0.dp, vertical: Dp = 0.dp) : this(
        start = horizontal,
        top = vertical,
        end = horizontal,
        bottom = vertical,
    )
}