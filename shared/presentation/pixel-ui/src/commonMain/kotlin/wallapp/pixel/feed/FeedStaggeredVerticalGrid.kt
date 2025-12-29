package wallapp.pixel.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import wallapp.log.Log
import wallapp.pixel.paging.PagingViewEvent
import wallapp.pixel.paging.PagingViewEventSink
import wallapp.pixel.render.Render
import wallapp.pixel.render.RenderViewId
import wallapp.pixel.util.ParallaxAlignmentVertical
import wallapp.pixel.util.ScrollDirection
import wallapp.pixel.view.DefaultViewSpecPreset.paddingDefault
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewAlignment
import wallapp.pixel.view.ViewVisibleState
import wallapp.pixel.view.ViewsVisibleListener

@Composable
private inline fun FeedAlignment(
    render: Render,
    view: View,
    rowRenderViewId: RenderViewId?,
    lazyStaggeredGridState: LazyStaggeredGridState,
): Alignment {
    if (rowRenderViewId == null) return Alignment.Center

    return when (val viewAlignment: ViewAlignment = render.viewAlignmentMapper.map(view)) {
        is ViewAlignment.ParallaxVertical -> {
            remember {
                ParallaxAlignmentVertical(
                    lazyStaggeredGridState = lazyStaggeredGridState,
                    key = rowRenderViewId,
                    maxParallaxScale = viewAlignment.maxScale,
                )
            }
        }
        else -> Alignment.Center
    }
}

private fun Modifier.applyLanePadding(
    state: LazyStaggeredGridState,
    view: View,
    index: Int,
    maxColumns: Int,
): Modifier {
    val gridItemInfo: LazyStaggeredGridItemInfo? = state.layoutInfo.visibleItemsInfo
        .firstOrNull { it.index == index }
    val lane = gridItemInfo?.lane
    
    val padding = if (!view.useZeroFeedPadding) {
        when {
            view.isMaxItemSpan -> { PaddingValues(start = paddingDefault, end = paddingDefault) }
            lane == 0 -> { PaddingValues(start = paddingDefault) }
            lane == maxColumns - 1 -> { PaddingValues(end = paddingDefault) }
            else -> { null }
        }
    } else {
        null
    }

    return if (padding != null) {
        this.padding(padding)
    } else {
        this
    }
}

@Composable
fun ObserveScrollProgressAndVisibleItems(
    state: LazyStaggeredGridState,
    views: List<View>,
    coroutineScope: CoroutineScope,
    viewsVisibleListener: ViewsVisibleListener,
) {
    var lastScrollState by remember { mutableStateOf(Pair(-1, -1)) }
    var lastVisibleViewsChangedStates by remember { mutableStateOf<List<ViewVisibleState>?>(null) }
    var lastScrollDirection by remember { mutableStateOf<ScrollDirection?>(null) }

    val visibleItemsFlow = snapshotFlow { state.filterVisibleItems(views) }
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

                val visibleItems = state.filterVisibleItems(views)
                viewsVisibleListener.onVisibleViewsSettled(visibleItems)
            }
        } else null

        onDispose {
            job?.cancel()
        }
    }
}

@Composable
internal fun FeedStaggeredVerticalGrid(
    render: Render,
    feedViewSpec: FeedViewSpec,
    views: List<View>,
    viewsVisibleListener: ViewsVisibleListener?,
    scrollToTop: SharedFlow<Unit>,
    modifier: Modifier = Modifier,
    feedState: FeedState?,
) {
    require(views.isNotEmpty()) { "Views must not be empty" }

    val paddingDefault = render.defaultViewSpec.paddingDefault
    val maxColumns = feedViewSpec.columns
    val renderViewIdFactory = render.renderViewIdFactory

    val pagingViewEventSink: PagingViewEventSink? = feedState?.pagingViewEventSink
    val defaultPageSize: Int? = feedState?.pagingDefaultPageSize
    val initialFeedScrollState: FeedScrollState? = feedState?.initialFeedScrollState
    val lastScrollStateUpdateSink: LastScrollStateUpdateSink? = feedState?.lastScrollStateUpdateSink
    val feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink? = feedState?.feedScrollPositionUpdateSink

    val state: LazyStaggeredGridState = rememberPersistableLazyStaggeredGridState(
        initialFirstVisibleItemIndex = initialFeedScrollState?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset = initialFeedScrollState?.firstVisibleItemScrollOffset ?: 0,
        lastScrollStateUpdateSink = lastScrollStateUpdateSink,
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(scrollToTop) {
        scrollToTop.collect {
            coroutineScope.launch {
                state.animateScrollToItem(index = 0)
            }
        }
    }

    val spanFactory: ((index: Int) -> StaggeredGridItemSpan) = { index ->
        val view = views[index]

        if (view.isMaxItemSpan) {
            StaggeredGridItemSpan.FullLine
        } else {
            StaggeredGridItemSpan.SingleLane
        }
    }

    val contentTypeFactory: (index: Int) -> Any? = { index ->
        val view = views[index]
        val viewState = view.viewState
        val viewSpec = view.viewSpec
        if (viewSpec != null) {
            viewState::class.simpleName + "<+>" + viewSpec::class.simpleName
        } else {
            viewState::class.simpleName
        }
    }

    val keyFactory: (index: Int) -> Any = { index ->
        renderViewIdFactory.createRenderViewId(views[index], index)
    }

//    Log.d("[$tag] isScrollable: ${state.isScrollable} isScrollInProgress: ${state.isScrollInProgress}, canScrollForward: ${state.canScrollForward}, canScrollBackward: ${state.canScrollBackward}")

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(count = maxColumns),
        modifier = modifier,
        state = state,
        horizontalArrangement = Arrangement.spacedBy(paddingDefault),
        verticalItemSpacing = paddingDefault,
    ) {
        items(
            count = views.size,
            key = keyFactory,
            contentType = contentTypeFactory,
            span = spanFactory,
        ) { index ->
            val view = views[index]
            val id = renderViewIdFactory.getRenderViewId(view, index)
            val alignment = FeedAlignment(render, view, id, state)

            View(
                render,
                view,
                Modifier.applyLanePadding(state, view, index, maxColumns),
                alignment = alignment,
            )
        }
    }

    if (viewsVisibleListener != null) {
        ObserveScrollProgressAndVisibleItems(state, views, coroutineScope, viewsVisibleListener)
    }

    if (pagingViewEventSink != null) {
        requireNotNull(defaultPageSize) { "defaultPageSize must not be null" }
        LazyStaggeredGridLoadMoreHandler(
            state,
            threshold = defaultPageSize / 3,
            onLoadMore = {
                pagingViewEventSink.invoke(PagingViewEvent.LoadMore)
            }
        )
    }

    if (feedScrollPositionUpdateSink != null) {
        FeedPositionHandler(
            state,
            onPositionUpdate = { position: FeedScrollPosition ->
                feedScrollPositionUpdateSink.invoke(position)
            }
        )
    }
}

@Composable
private inline fun LazyStaggeredGridLoadMoreHandler(
    lazyListState: LazyStaggeredGridState,
    threshold: Int,
    crossinline onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastViewIndex = layoutInfo.totalItemsCount - 1
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)
            lastVisibleItemIndex >= (lastViewIndex - threshold)
        }
    }

    LaunchedEffect(true) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onLoadMore()
            }
    }
}

private fun LazyStaggeredGridState.filterVisibleItems(items: List<View>): List<ViewVisibleState> {
    val layoutInfo = this.layoutInfo
    val visibleItems = mutableListOf<ViewVisibleState>()

    layoutInfo.visibleItemsInfo.forEach { itemInfo ->
        val visibleIndex = itemInfo.index
        visibleItems.add(
            ViewVisibleState(
                visibleIndex = visibleIndex,
                viewId = items[visibleIndex].viewState.viewId,
            )
        )
    }

    return visibleItems
}

@Composable
fun rememberPersistableLazyStaggeredGridState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
    lastScrollStateUpdateSink: LastScrollStateUpdateSink? = null,
): LazyStaggeredGridState {
    val state = rememberLazyStaggeredGridState(
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

@Composable
private fun FeedPositionHandler(
    state: LazyStaggeredGridState,
    onPositionUpdate: (FeedScrollPosition) -> Unit,
) {
    val position = remember(state) {
        derivedStateOf {
            when {
                !state.canScrollBackward -> FeedScrollPosition.Top
                !state.canScrollForward -> FeedScrollPosition.Bottom
                else -> FeedScrollPosition.Middle
            }
        }
    }

    LaunchedEffect(true) {
        snapshotFlow { position.value }
            .distinctUntilChanged()
            .onEach { Log.d("FeedPositionHandler: $it") }
            .collect {
                onPositionUpdate(it)
            }
    }
}
