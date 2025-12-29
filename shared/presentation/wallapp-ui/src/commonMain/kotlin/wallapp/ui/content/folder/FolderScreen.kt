package wallapp.ui.content.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.folder.FolderViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.pixel.util.BoxWithFooterScrim
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.widget.SurfaceThemeFix

@Composable
fun FolderScreen(
    render: Render,
    viewState: FolderViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is FolderViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            FolderViewState.Loading -> {}
            is FolderViewState.Success -> FolderScreen(render, viewState, contentModifier)
        }
    }
}

@Composable
fun FolderScreen(
    render: Render,
    viewState: FolderViewState.Success,
    modifier: Modifier = Modifier,
) {
    SurfaceThemeFix {
        BoxWithFooterScrim(
            render,
            modifier = modifier,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                FolderToolbar(
                    render,
                    viewState.toolbarViewState,
                    viewState.viewSpec.toolbarViewSpec
                )

                FeedGrid(
                    render,
                    viewState.feedViewState,
                    modifier,
                )
            }
        }
    }
}