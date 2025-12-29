package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.graphics.Color
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.view.ViewState

@Immutable
class HomeTopBarViewState(
    val viewSpec: HomeTopBarViewSpec,
    val messageBar: MessageBarViewState?,
    val containerColor: Color,
    val profileImage: ProfileImageViewState,
    val tabs: TabsViewState,
) : ViewState
