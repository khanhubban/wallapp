package wallapp.ui.content.connections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.connections.ConnectionsViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.tab.TabbedContent
import wallapp.pixel.toolbar.Toolbar
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.widget.SurfaceThemeFix


@Composable
fun ConnectionsScreen(
    render: Render,
    viewState: ConnectionsViewState,
    modifier: Modifier = Modifier,
) {
    Surface {
        ContentWithLoading(
            render,
            loadingIsVisible = viewState is ConnectionsViewState.Loading,
            modifier = modifier,
        ) { contentModifier ->
            when (viewState) {
                ConnectionsViewState.Loading -> {}

                is ConnectionsViewState.Success -> {
                    ConnectionsSuccessScreen(render, viewState, contentModifier)
                }
            }
        }
    }
}

@Composable
fun ConnectionsSuccessScreen(
    render: Render,
    viewState: ConnectionsViewState.Success,
    modifier: Modifier = Modifier,
) {
    SurfaceThemeFix {
        ConnectionsScreenContent(render, viewState, modifier)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConnectionsScreenContent(
    render: Render,
    viewState: ConnectionsViewState.Success,
    modifier: Modifier = Modifier,
) {
    val tabs = viewState.tabs
    val toolbarViewState = viewState.toolbarViewState

    Column(
        modifier.fillMaxSize()
            .statusBarsPadding(render.windowFrame),
    ) {
        Toolbar(render, toolbarViewState)
        TabbedContent(render, tabs)
    }
}
