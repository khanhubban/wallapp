package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewState

@Immutable
data class ProfileConnectionsViewState(
    val connections: List<ProfileConnectionViewState>,
) : ViewState