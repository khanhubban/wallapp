package wallapp.content.state.follow

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.view.ViewState

@Immutable
data class FollowIndicatorViewState(
    val visible: Boolean,
    val icon: Image,
    val background: Image,
) : ViewState
