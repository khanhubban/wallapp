package wallapp.ui.content.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render
import wallapp.ui.content.loading.ContentWithLoading

@Composable
fun HomeOnboardingScreen(
    render: Render,
    viewState: HomeOnboardingViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is HomeOnboardingViewState.Loading,
        modifier = modifier,
    ) {contentModifier ->
        when (viewState) {
            is HomeOnboardingViewState.Loading -> { }

            is HomeOnboardingViewState.Success -> {
                HomeOnboardingScreen(render, viewState, contentModifier)
            }
        }
    }
}

@Composable
fun HomeOnboardingScreen(
    render: Render,
    viewState: HomeOnboardingViewState.Success,
    modifier: Modifier = Modifier,
) {
    val feed = viewState.feedViewState
    FeedGrid(
        render,
        feed,
        modifier = modifier,
    )
}

