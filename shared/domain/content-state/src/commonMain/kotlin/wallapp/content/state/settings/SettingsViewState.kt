package wallapp.content.state.settings

import androidx.compose.runtime.Immutable
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.toolbar.ToolbarViewState


@Immutable
data class SettingsViewState(
    val toolbarViewState: ToolbarViewState,
    val settingViewStates: List<SettingViewState>,
    /**
     * Set to [true] if this [SettingsViewState] will display on a full screen. [false] is used
     * when this [SettingsViewState] is displayed on UI such as a bottom sheet.
     */
    val fullScreenToolbarOffset: Boolean,
    val feedScrollStateWrapper: FeedScrollStateWrapper,
): ScreenViewState
