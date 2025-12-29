package wallapp.ui.content.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.settings.SettingGroupViewState
import wallapp.pixel.render.Render

@Composable
fun SettingGroup(
    render: Render,
    viewState: SettingGroupViewState,
    modifier: Modifier = Modifier,
) {
    val settingViewStates = viewState.settingViewStates

    Column(
        modifier = modifier,
    ) {
        settingViewStates.forEach {
            Setting(render, viewState = it)
        }
    }
}