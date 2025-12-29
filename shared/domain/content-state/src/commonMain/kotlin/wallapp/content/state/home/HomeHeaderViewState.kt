package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.pixel.selection.SelectionGroupViewState
import wallapp.pixel.view.ViewState

@Immutable
data class HomeHeaderViewState(
    val viewSpec: HomeHeaderViewSpec,
    val profileImage: ProfileImageViewState,
    val filterGroup: SelectionGroupViewState,
) : ViewState
