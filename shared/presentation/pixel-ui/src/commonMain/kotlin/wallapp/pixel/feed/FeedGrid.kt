package wallapp.pixel.feed

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import wallapp.pixel.compose.conditional
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.input.NestedScrollConsumingFeedController
import wallapp.pixel.input.NestedScrollController
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.statusbar.StatusBar
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.Toolbar
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewState
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.theme.ColorToken
import kotlin.math.roundToInt

val View.gridSpan: (LazyGridItemSpanScope.() -> GridItemSpan)?
    get() {
        return if (isMaxItemSpan) {
            { GridItemSpan(maxCurrentLineSpan) }
        } else {
            null
        }
    }

@Composable
fun FeedGrid(
    render: Render,
    viewState: FeedViewState,
    modifier: Modifier = Modifier,
    toolbarNestedScrollController: NestedScrollController? = null,
    feedNestedScrollController: NestedScrollConsumingFeedController? = null,
    statusBarAlpha: Float = 1f,
) {
    val scrollToTop: SharedFlow<Unit> = viewState.scrollToTop

    FeedGrid(
        render,
        toolbar = viewState.toolbar,
        statusBarColor = viewState.statusBarColor,
        applyStatusBarOffsetForToolbar = viewState.applyStatusBarOffsetForToolbar,
        feedViewSpec = viewState.feedViewSpec,
        views = viewState.views,
        viewsVisibleListener = viewState.viewsVisibleListener,
        scrollToTop = scrollToTop,
        modifier = modifier,
        feedState = viewState.feedState,
        messageBarHeight = viewState.messageBarHeight,
        toolbarNestedScrollController = toolbarNestedScrollController,
        feedNestedScrollController = feedNestedScrollController,
        statusBarAlpha = statusBarAlpha,
    )
}

@Composable
private fun FeedGrid(
    render: Render,
    toolbar: ViewState?,
    statusBarColor: ColorToken?,
    applyStatusBarOffsetForToolbar: Boolean,
    feedViewSpec: FeedViewSpec,
    views: List<View>,
    viewsVisibleListener: ViewsVisibleListener?,
    scrollToTop: SharedFlow<Unit>,
    feedState: FeedState?,
    modifier: Modifier = Modifier,
    messageBarHeight: Dp = 0.dp,
    toolbarNestedScrollController: NestedScrollController?,
    feedNestedScrollController: NestedScrollConsumingFeedController?,
    statusBarAlpha: Float,
) {
    val toolbarScrollController: NestedScrollController? = toolbarNestedScrollController ?:
        LocalFeedToolbarNestedScrollController.current
    val toolbarYOffset = toolbarScrollController?.currentYOffsetPx?.roundToInt()
    val toolbarOffset = toolbarYOffset?.let { IntOffset(x = 0, y = it) } ?: IntOffset.Zero

    val feedScrollController: NestedScrollConsumingFeedController? = feedNestedScrollController
    val feedYOffset = feedScrollController?.currentYOffsetPx?.roundToInt()
    val feedOffset = feedYOffset?.let { IntOffset(x = 0, y = it) } ?: IntOffset.Zero

    val animatedMessageBarHeight by animateDpAsState(
        targetValue = messageBarHeight,
        animationSpec = tween(durationMillis = 500),
    )

    val offsetYAnimated by animateDpAsState(
        targetValue = feedViewSpec.offsetYAnimated,
        animationSpec = tween(durationMillis = CollapsingToolbarAnimationDuration),
    )

    val toolbarModifier = (if (applyStatusBarOffsetForToolbar) {
        Modifier.statusBarsPadding(render.windowFrame)
    } else {
        Modifier
    }).offset { toolbarOffset }

    val feedShape = feedViewSpec.shapeSpec?.let {
        render.shapeMapperComposable.map(it)
    }
    val feedModifier = Modifier
        .offset { feedOffset }
        .offset(y = offsetYAnimated)
        .padding(top = animatedMessageBarHeight)
        .conditional(feedShape != null) { clip(feedShape!!) }
        .background(ThemeColorTypeMapper.map(ColorToken.ThemeBackground))

    Box(
        modifier.fillMaxSize(),
    ) {
        FeedGrid(
            render,
            feedViewSpec,
            views,
            viewsVisibleListener,
            scrollToTop,
            feedState,
            feedModifier,
        )
        if (toolbar != null) {
            Toolbar(
                render,
                toolbar,
                modifier = toolbarModifier,
            )

            if (statusBarColor != null) {
                StatusBar(render, color = ThemeColorTypeMapper.map(statusBarColor), alpha = statusBarAlpha)
            } else if (applyStatusBarOffsetForToolbar) {
                StatusBar(render, alpha = statusBarAlpha)
            }
        }
    }
}

@Composable
private fun FeedGrid(
    render: Render,
    feedViewSpec: FeedViewSpec,
    views: List<View>,
    viewsVisibleListener: ViewsVisibleListener?,
    scrollToTop: SharedFlow<Unit>,
    feedState: FeedState?,
    modifier: Modifier = Modifier,
) {
    val useStaggeredGrid = feedViewSpec.staggeredGrid
    if (useStaggeredGrid) {
        FeedStaggeredVerticalGrid(
            render = render,
            feedViewSpec = feedViewSpec,
            views = views,
            viewsVisibleListener = viewsVisibleListener,
            scrollToTop = scrollToTop,
            feedState = feedState,
            modifier = modifier,
        )
    } else {
        FeedVerticalGrid(
            render,
            feedViewSpec,
            views,
            viewsVisibleListener,
            scrollToTop,
            feedState,
            modifier,
        )
    }
}

const val CollapsingToolbarAnimationDuration = 250 // millis