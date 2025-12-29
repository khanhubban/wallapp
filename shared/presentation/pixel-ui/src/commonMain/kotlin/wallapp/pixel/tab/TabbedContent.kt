package wallapp.pixel.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.pager.PagerPersistableState
import wallapp.pixel.pager.UpdateLastPagerStateEvent
import wallapp.pixel.render.Render
import wallapp.pixel.view.View

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabbedContent(
    render: Render,
    tabs: TabsViewState,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(initialPage = tabs.initialIndex) { tabs.size },
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        TabbedContentHorizontalPager(render, pagerState, tabs)
        Tabs(
            render,
            tabs,
            pagerState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabbedContentHorizontalPager(
    render: Render,
    pagerState: PagerState,
    tabs: TabsViewState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
    ) { page ->
        val view = tabs.tabs[page].view ?: return@HorizontalPager
        View(
            render,
            view,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberPersistablePagerState(
    initialPage: Int = 0,
    initialPageOffsetFraction: Float = 0f,
    lastPagerStateUpdateSink: LastPagerStateUpdateSink? = null,
    pageCount: () -> Int,
): PagerState {
    val rememberedInitialPageOffsetFraction = remember { initialPageOffsetFraction }
    val state: PagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = initialPageOffsetFraction,
        pageCount = pageCount,
    )

    DisposableEffect(lastPagerStateUpdateSink) {
        onDispose {
            lastPagerStateUpdateSink?.invoke(
                UpdateLastPagerStateEvent(
                    PagerPersistableState(
                        initialPage = state.currentPage,
                        initialPageOffsetFraction = rememberedInitialPageOffsetFraction,
                    )
                )
            )
        }
    }

    return state
}
