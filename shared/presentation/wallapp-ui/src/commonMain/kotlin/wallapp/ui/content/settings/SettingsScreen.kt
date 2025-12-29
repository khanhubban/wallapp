package wallapp.ui.content.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.settings.SettingsViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.toolbar.Toolbar

@Composable
fun SettingsScreen(
    render: Render,
    viewState: SettingsViewState,
    modifier: Modifier = Modifier,
) {
    if (viewState.fullScreenToolbarOffset) {
        SettingsFullScreen(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    } else {
        SettingsPanel(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    }
}

@Composable
fun SettingsFullScreen(
    render: Render,
    viewState: SettingsViewState,
    modifier: Modifier = Modifier,
) {
    val settingViewStates = viewState.settingViewStates

    Surface(modifier = modifier) {
        Scaffold(
            topBar = { SettingsToolbar(render, viewState) },
        ) { padding ->
            SettingsFeed(
                render = render,
                viewStates = settingViewStates,
                modifier = Modifier.padding(padding),
                feedScrollStateWrapper = viewState.feedScrollStateWrapper,
            )
        }
    }
}

@Composable
fun SettingsPanel(
    render: Render,
    viewState: SettingsViewState,
    modifier: Modifier = Modifier,
) {
    val settingViewStates = viewState.settingViewStates
    val toolbarViewState = viewState.toolbarViewState
    val toolbarHeight = toolbarViewState.height
    val panelTopOffset = 16.dp

    Surface {
        Box(modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = panelTopOffset),
        ) {
            SettingsToolbar(render, viewState)
            SettingsFeed(
                render = render,
                viewStates = settingViewStates,
                modifier = Modifier.padding(top = toolbarHeight),
                feedScrollStateWrapper = viewState.feedScrollStateWrapper,
            )
        }
    }
}

@Composable
fun SettingsToolbar(
    render: Render,
    viewState: SettingsViewState,
    modifier: Modifier = Modifier,
) {
    val toolbarViewState = viewState.toolbarViewState
    val fullScreenToolbarOffset = viewState.fullScreenToolbarOffset

    if (fullScreenToolbarOffset) {
        Toolbar(
            render,
            toolbarViewState,
            modifier = modifier.statusBarsPadding(render.windowFrame),
        )
    } else {
        Toolbar(
            render,
            toolbarViewState,
            modifier = modifier,
            useDefaultWindowInsets = false,
        )
    }
}
