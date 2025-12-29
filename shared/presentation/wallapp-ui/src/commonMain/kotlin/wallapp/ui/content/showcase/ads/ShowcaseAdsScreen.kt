package wallapp.ui.content.showcase.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.showcase.ads.ShowcaseAdsViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.loading.ContentWithLoading


@Composable
fun ShowcaseAdsScreen(
    render: Render,
    viewState: ShowcaseAdsViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is ShowcaseAdsViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            is ShowcaseAdsViewState.Loading -> {}

            is ShowcaseAdsViewState.Data -> {
                FeedGrid(
                    render,
                    viewState.feedViewState,
                    contentModifier,
                )
            }
        }
    }
}
