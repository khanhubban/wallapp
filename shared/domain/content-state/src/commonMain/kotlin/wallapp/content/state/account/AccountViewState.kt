package wallapp.content.state.account

import androidx.compose.runtime.Immutable
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.settings.SettingViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.text.Text
import wallapp.pixel.toolbar.ToolbarViewState

@Immutable
data class AccountViewState(
    val toolbarViewState: ToolbarViewState,
    val profileImage: ProfileImageViewState?,
    val displayName: Text?,
    val email: Text?,
    val settings: List<SettingViewState>,
) : ScreenViewState
