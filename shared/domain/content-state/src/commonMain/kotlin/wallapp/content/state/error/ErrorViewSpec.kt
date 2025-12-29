package wallapp.content.state.error

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class ErrorViewSpec(
    val imageSize: Dp,
) : ViewSpec
