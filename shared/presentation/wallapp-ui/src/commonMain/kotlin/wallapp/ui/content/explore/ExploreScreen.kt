package wallapp.ui.content.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.explore.HighlightCarouselViewState
import wallapp.content.state.explore.UpdateLastFeedOffsetEvent
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.feed.LocalExploreFeedToolbarNestedScrollController
import wallapp.pixel.feed.LocalFeedContentNestedScrollController
import wallapp.pixel.render.Render
import wallapp.ui.content.highlightcarousel.HighlightsCarousel
import wallapp.ui.content.loading.ContentWithLoading

@Composable
fun ExploreScreen(
    render: Render,
    viewState: ExploreViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is ExploreViewState.Loading,
        modifier = modifier,
    ) {contentModifier ->
        when (viewState) {
            is ExploreViewState.Loading -> { }

            is ExploreViewState.Success -> {
                ExploreFeedScreenSuccess(render, viewState, contentModifier)
            }
        }
    }
}


@Composable
fun ExploreFeedScreenSuccess(
    render: Render,
    viewState: ExploreViewState.Success,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        val feedNestedScrollController = LocalFeedContentNestedScrollController.current
        val toolbarNestedScrollController = LocalExploreFeedToolbarNestedScrollController.current
        val highlightCarouselViewState = viewState.highlightCarouselViewState
        val statusBarBackgroundAlphaOnChange = viewState.statusBarBackgroundAlphaOnChange

        var statusBarAlpha by remember { mutableStateOf(1f) }
        var previousStatusBarAlpha by remember { mutableStateOf(-1f) }

        if (highlightCarouselViewState != null) {
            statusBarAlpha = feedNestedScrollController?.let {
                calculateStatusBarAlpha(
                    offset = feedNestedScrollController.currentYOffsetPx,
                    render = render,
                    highlightCarouselViewState = highlightCarouselViewState,
                )
            } ?: 1f

            HighlightsCarousel(
                render,
                highlightCarouselViewState,
            )
        }

        if (statusBarAlpha != previousStatusBarAlpha) {
            statusBarBackgroundAlphaOnChange(statusBarAlpha)
            previousStatusBarAlpha = statusBarAlpha
        }

        FeedGrid(
            render,
            viewState.feedViewState,
            feedNestedScrollController = feedNestedScrollController,
            toolbarNestedScrollController = toolbarNestedScrollController,
            statusBarAlpha = statusBarAlpha,
        )

        val lastFeedOffsetUpdateSink = viewState.lastFeedOffsetUpdateSink
        DisposableEffect(lastFeedOffsetUpdateSink) {
            onDispose {
                lastFeedOffsetUpdateSink.invoke(
                    UpdateLastFeedOffsetEvent(
                        lastFeedOffset = feedNestedScrollController?.currentYOffsetPx ?: 0f
                    )
                )
            }
        }
    }
}

@Composable
private fun calculateStatusBarAlpha(offset: Float, render: Render, highlightCarouselViewState: HighlightCarouselViewState): Float {
    val statusBarHeightPx = with(LocalDensity.current) { render.windowFrame.statusBarHeight.toPx() }
    val carouselHeightPx = with(LocalDensity.current) { highlightCarouselViewState.carouselViewSpec.height.toPx() }
    val threshold = statusBarHeightPx + ((carouselHeightPx - statusBarHeightPx) * 0.2f)
    val alpha = if (offset > threshold) {
        0f
    } else {
        (threshold - offset) / (threshold - statusBarHeightPx)
    }
    return alpha.coerceIn(0f, 1f)
}
