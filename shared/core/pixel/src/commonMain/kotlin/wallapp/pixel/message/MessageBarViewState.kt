package wallapp.pixel.message

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewState

@Immutable
data class MessageBarViewState(
    val viewSpec: MessageBarViewSpec,
    val content: View,
) : ViewState
