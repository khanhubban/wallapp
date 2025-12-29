package wallapp.pixel.message

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class MessageBarViewSpec(
    val maxHeight: Dp,
) : ViewSpec
