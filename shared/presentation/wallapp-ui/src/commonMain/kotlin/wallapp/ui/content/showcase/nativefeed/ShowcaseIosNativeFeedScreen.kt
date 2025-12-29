package wallapp.ui.content.showcase.nativefeed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.loading.ContentWithLoading


@Composable
fun ShowcaseIosNativeFeedScreen(
    render: Render,
    viewState: ShowcaseIosNativeFeedViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is ShowcaseIosNativeFeedViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            is ShowcaseIosNativeFeedViewState.Loading -> {}

            is ShowcaseIosNativeFeedViewState.Data -> {
                FeedGrid(
                    render,
                    viewState.feedViewState,
                    contentModifier,
                )
            }
        }
    }
}
