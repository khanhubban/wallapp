package wallapp.pixel.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import wallapp.pixel.render.Render
import wallapp.pixel.util.ScrollDirection
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewVisibleState
import wallapp.pixel.view.ViewsVisibleListener

@Composable
internal fun FeedVerticalGrid(
    render: Render,
    feedViewSpec: FeedViewSpec,
    views: List<View>,
    viewsVisibleListener: ViewsVisibleListener?,
    scrollToTop: SharedFlow<Unit>,
    feedState: FeedState?,
    modifier: Modifier = Modifier,
) {
    require(views.isNotEmpty()) { "Views must not be empty" }

    val initialFeedScrollState = feedState?.initialFeedScrollState
    val lastScrollStateUpdateSink = feedState?.lastScrollStateUpdateSink

    val lazyGridState: LazyGridState = rememberPersistableLazyGridState(
        initialFirstVisibleItemIndex = initialFeedScrollState?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset = initialFeedScrollState?.firstVisibleItemScrollOffset ?: 0,
        lastScrollStateUpdateSink = lastScrollStateUpdateSink,
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(scrollToTop) {
        scrollToTop.collect {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(index = 0)
            }
        }
    }

    val viewRows: List<FeedRow> = views.formatToRows(render.renderViewIdFactory, feedViewSpec)
    val itemWidth = feedViewSpec.itemWidth
    val itemSpacingVertical = feedViewSpec.itemSpacingVertical

    if (viewsVisibleListener != null) {
        ObserveScrollProgressAndVisibleItems(
            lazyGridState,
            viewRows,
            coroutineScope,
            viewsVisibleListener,
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = itemWidth),
        state = lazyGridState,
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(itemSpacingVertical),
    ) {
        viewRows.forEach { feedRow ->
            item(span = feedRow.gridSpan, key = feedRow.renderViewId) {
                FeedRow(
                    render,
                    feedViewSpec,
                    feedRow,
                    lazyGridState,
                )
            }
        }
//        itemsIndexed(
//            items = viewRows,
//            key = { index, feedRow -> feedRow.viewId ?: index },
//            span = { _, feedRow -> feedRow.gridItemSpan(maxCurrentLineSpan) }
//        ) { index, feedRow ->
//            FeedRow(
//                render,
//                feedViewSpec,
//                feedRow,
//                lazyGridState,
//            )
//        }
    }
}

@Composable
fun rememberPersistableLazyGridState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
    lastScrollStateUpdateSink: LastScrollStateUpdateSink? = null,
): LazyGridState {
    val state = rememberLazyGridState(
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
    )

    DisposableEffect(lastScrollStateUpdateSink) {
        onDispose {
            lastScrollStateUpdateSink?.invoke(
                UpdateLastScrollStateEvent(
                    FeedScrollState(
                        firstVisibleItemIndex = state.firstVisibleItemIndex,
                        firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset,
                    )
                )
            )
        }
    }

    return state
}

private fun LazyGridState.filterVisibleItems(feedRows: List<FeedRow>): List<ViewVisibleState> {
    val layoutInfo = this.layoutInfo
    val visibleItems = mutableListOf<ViewVisibleState>()
    var mappedVisibleIndex = 0

    layoutInfo.visibleItemsInfo.forEach { itemInfo ->
        val visibleIndex = itemInfo.index
        val feedRow = feedRows[visibleIndex]

        feedRow.views.forEach {
            visibleItems.add(
                ViewVisibleState(
                    visibleIndex = ++mappedVisibleIndex,
                    viewId = it.viewState.viewId,
                )
            )
        }
    }

    return visibleItems
}


@Composable
private fun ObserveScrollProgressAndVisibleItems(
    state: LazyGridState,
    feedRows: List<FeedRow>,
    coroutineScope: CoroutineScope,
    viewsVisibleListener: ViewsVisibleListener,
) {
    var lastScrollState by remember { mutableStateOf(Pair(-1, -1)) }
    var lastVisibleViewsChangedStates by remember { mutableStateOf<List<ViewVisibleState>?>(null) }
    var lastScrollDirection by remember { mutableStateOf<ScrollDirection?>(null) }

    val visibleItemsFlow = snapshotFlow { state.filterVisibleItems(feedRows) }
        .distinctUntilChanged()

    LaunchedEffect(visibleItemsFlow) {
        visibleItemsFlow.collect { visibleStates ->
            val currentScrollState = Pair(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset)

            val direction = when {
                lastScrollState.first < 0 -> null // Initial state
                currentScrollState.first > lastScrollState.first ||
                        (currentScrollState.first == lastScrollState.first && currentScrollState.second > lastScrollState.second) -> ScrollDirection.Descending
                currentScrollState.first < lastScrollState.first ||
                        (currentScrollState.first == lastScrollState.first && currentScrollState.second < lastScrollState.second) -> ScrollDirection.Ascending
                else -> lastScrollDirection // Maintain the last direction if there's no change
            }

            if (direction != null && direction != lastScrollDirection) {
                viewsVisibleListener.onScrollDirectionChanged(direction)
                lastScrollDirection = direction
            }
            lastScrollState = currentScrollState

            if (state.isScrollInProgress) {
                if (lastVisibleViewsChangedStates != visibleStates) {
                    viewsVisibleListener.onVisibleViewsChanged(visibleStates)
                    lastVisibleViewsChangedStates = visibleStates
                }
            }
        }
    }

    DisposableEffect(state.isScrollInProgress) {
        val job = if (!state.isScrollInProgress) {
            coroutineScope.launch {
                lastScrollDirection = null
                viewsVisibleListener.onScrollDirectionChanged(lastScrollDirection)

                val visibleItems = state.filterVisibleItems(feedRows)
                viewsVisibleListener.onVisibleViewsSettled(visibleItems)
            }
        } else null

        onDispose {
            job?.cancel()
        }
    }
}