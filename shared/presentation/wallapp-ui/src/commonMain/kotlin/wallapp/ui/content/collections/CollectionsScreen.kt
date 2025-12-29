package wallapp.ui.content.collections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.collections.CollectionsViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.error.ErrorScreen
import wallapp.ui.content.loading.ContentWithLoading

@Composable
fun CollectionsScreen(
    render: Render,
    collectionsViewState: CollectionsViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = collectionsViewState is CollectionsViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (collectionsViewState) {
            CollectionsViewState.Loading -> {}

            is CollectionsViewState.Ready -> {
                CollectionsReadyScreen(render, collectionsViewState, contentModifier)
            }

            CollectionsViewState.Error -> {
                ErrorScreen(render, contentModifier)
            }
        }
    }
}

@Composable
fun CollectionsReadyScreen(
    render: Render,
    viewState: CollectionsViewState.Ready,
    modifier: Modifier = Modifier,
) {
    FeedGrid(
        render,
        viewState.feedViewState,
        modifier,
    )
}