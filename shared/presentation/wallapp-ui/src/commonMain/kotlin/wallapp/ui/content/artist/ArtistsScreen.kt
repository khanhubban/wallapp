package wallapp.ui.content.artist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.artist.ArtistsViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.loading.ContentWithLoading

@Composable
fun ArtistsScreen(
    render: Render,
    viewState: ArtistsViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is ArtistsViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            is ArtistsViewState.Loading -> {}

            is ArtistsViewState.Success -> {
                ArtistsScreen(render, viewState, contentModifier)
            }
        }
    }
}

@Composable
fun ArtistsScreen(
    render: Render,
    viewState: ArtistsViewState.Success,
    modifier: Modifier = Modifier,
) {
    FeedGrid(
        render,
        viewState.feedViewState,
        modifier,
    )
}