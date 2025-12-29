package wallapp.pixel.util

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class OffsetViewSpec(
    val top: Dp,
    val height: Dp,
) : ViewSpec {
    val bottom: Dp
        get() = top + height
}

fun Modifier.offset(viewSpec: OffsetViewSpec): Modifier =
    this.offset(y = viewSpec.top)