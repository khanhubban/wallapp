package wallapp.ui.content.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import wallapp.content.state.search.SearchResultsViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.loading.ContentWithLoading

@Composable
fun SearchResultsScreen(
    render: Render,
    viewState: SearchResultsViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is SearchResultsViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            is SearchResultsViewState.Loading -> {}

            is SearchResultsViewState.Data -> {
                SearchResultsScreen(
                    render = render,
                    viewState = viewState,
                    modifier = contentModifier,
                )
            }

            is SearchResultsViewState.Inactive -> {}
        }
    }
}

@Composable
fun SearchResultsScreen(
    render: Render,
    viewState: SearchResultsViewState.Data,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        FeedGrid(
            render,
            viewState.feedViewState,
            modifier
                .haze(hazeState),
        )
        SearchResultsHeader(
            render,
            viewState.headerViewState,
            Modifier
                .align(Alignment.TopCenter),
            hazeState
        )
    }
}
