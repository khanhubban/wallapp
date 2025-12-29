package wallapp.ui.content.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import wallapp.content.state.home.HomeViewState
import wallapp.pixel.compose.dpToPx
import wallapp.pixel.feed.LocalFeedToolbarNestedScrollController
import wallapp.pixel.input.NestedScrollController
import wallapp.pixel.render.Render
import wallapp.pixel.statusbar.StatusBar
import wallapp.pixel.tab.TabbedContentHorizontalPager
import wallapp.pixel.tab.rememberPersistablePagerState
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.content.widget.NoDataScreen
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    render: Render,
    viewState: HomeViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is HomeViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            is HomeViewState.NoData -> {
                HomeNoDataScreen(render, viewState, contentModifier)
            }

            is HomeViewState.Data -> {
                HomeScreen(render, viewState, contentModifier)
            }

            HomeViewState.Loading -> {}
        }
    }
}

@Composable
fun HomeNoDataScreen(
    render: Render,
    viewState: HomeViewState.NoData,
    modifier: Modifier = Modifier,
) {
    NoDataScreen(
        render,
        viewState.viewState,
        modifier = modifier.fillMaxWidth()
            .padding(32.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    render: Render,
    viewState: HomeViewState.Data,
    modifier: Modifier = Modifier,
) {
    val statusBarHeight = render.windowFrame.statusBarHeight
    val tabs = viewState.tabs
    val homeTopBar = viewState.topBar
    val pagerState: PagerState = rememberPersistablePagerState(
        initialPage = tabs.initialIndex,
        lastPagerStateUpdateSink = viewState.lastPagerStateUpdateSink
    ) { tabs.size }

    val messageBarHeight = viewState.topBar.messageBar?.viewSpec?.maxHeight ?: 0.dp
    val topBarScrollController: NestedScrollController? =
        LocalFeedToolbarNestedScrollController.current
    val topBarYOffset = topBarScrollController?.currentYOffsetPx?.roundToInt()
    val topBarOffset = topBarYOffset?.let { IntOffset(x = 0, y = it) } ?: IntOffset.Zero
    val adjustedTopBarOffset =
        topBarOffset.copy(y = topBarOffset.y + statusBarHeight.dpToPx().toInt())

    Box(
        modifier.fillMaxSize(),
    ) {
        TabbedContentHorizontalPager(
            render,
            pagerState,
            tabs,
        )

        HomeTopBar(
            render,
            homeTopBar,
            pagerState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset { adjustedTopBarOffset },
            messageBarHeight,
        )

        StatusBar(render)
    }
}
