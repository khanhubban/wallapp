package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewState

@Immutable
data class ProfileConnectionViewState(
    val label: String,
    val count: String,
    val onClick: () -> Unit,
) : ViewState
