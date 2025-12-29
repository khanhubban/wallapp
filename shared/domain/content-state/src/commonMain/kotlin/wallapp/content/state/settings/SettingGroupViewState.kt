package wallapp.content.state.settings

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewState

@Immutable
data class SettingGroupViewState(
    val settingViewStates: List<SettingViewState>,
) : ViewState