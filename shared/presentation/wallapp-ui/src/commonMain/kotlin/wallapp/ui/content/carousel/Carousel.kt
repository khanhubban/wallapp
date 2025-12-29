package wallapp.ui.content.carousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import wallapp.content.state.carousel.CarouselPageViewState
import wallapp.content.state.carousel.CarouselViewSpec
import wallapp.content.state.carousel.CarouselViewState
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.pager.TransformPagerEdgeItems
import wallapp.pixel.render.Render
import wallapp.pixel.view.View
import kotlin.math.max

@Composable
fun Carousel(
    render: Render,
    viewState: CarouselViewState,
    viewSpec: CarouselViewSpec,
    modifier: Modifier = Modifier,
) {
    CarouselPager(
        render = render,
        viewState = viewState,
        viewSpec = viewSpec,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselPager(
    render: Render,
    viewState: CarouselViewState,
    viewSpec: CarouselViewSpec,
    modifier: Modifier = Modifier,
) {
    val height = viewSpec.height
    val pageWidth = viewSpec.pageWidth
    val pageSpacing = viewSpec.pageSpacing
    val edgeButtonWidth = viewSpec.edgeButtonWidth
    val padding = viewSpec.padding
    val applyInfiniteScroll = viewSpec.applyInfiniteScroll

    val items = viewState.pages

    val (pageCount, initialPage) = if (applyInfiniteScroll) {
        // Previously, we used a max of Int.MAX_VALUE, but this caused issues hangs when
        // using Compose 1.6.0. We now specify a maximum number of items to avoid this. #740.
        val maxItems = 128
        require(items.size < maxItems)
        val repetitions = max(maxItems / items.size, 1)

        val finitePageCount = items.size * repetitions
        val finiteInitialPage = ((finitePageCount / 2) - ((finitePageCount / 2) % items.size)) + (viewState.initialPage % items.size)
        finitePageCount to finiteInitialPage
    } else {
        items.size to viewState.initialPage
    }
    val pagerHorizontalOffset = if (viewSpec.centerPage) {
        pageSpacing + edgeButtonWidth
    } else {
        0.dp
    }

    val pagerState: PagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    val coroutineScope = rememberCoroutineScope()
    val nextPageOnClick = {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }
    val previousPageOnClick = {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
            .height(height)
            .paddingAx(padding),
    ) {
        HorizontalPager(
            pageSpacing = pageSpacing,
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = pagerHorizontalOffset),
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier.fillMaxSize(),
        ) { page: Int ->
            val index = page % items.size
            val item = items[index]

            CarouselPage(
                render = render,
                viewSpec = viewSpec,
                page = page,
                pagerState = pagerState,
                item = item,
                itemsSize = items.size,
            )
        }

        if (edgeButtonWidth > 0.dp) {
            Box(
                modifier = Modifier.fillMaxHeight()
                    .width(edgeButtonWidth)
                    .align(Alignment.CenterStart)
                    .clickableNoRipple { previousPageOnClick() },
            )
            Box(
                modifier = Modifier.fillMaxHeight()
                    .width(edgeButtonWidth)
                    .align(Alignment.CenterEnd)
                    .clickableNoRipple { nextPageOnClick() },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerScope.CarouselPage(
    render: Render,
    viewSpec: CarouselViewSpec,
    page: Int,
    pagerState: PagerState,
    item: CarouselPageViewState,
    itemsSize: Int,
) {
    val transformEdgeItems = viewSpec.transformEdgeItems
    val itemView = item.view

    if (transformEdgeItems) {
        TransformPagerEdgeItems(
            page = page,
            pagerState = pagerState,
            itemsSize = itemsSize,
        ) { scale, alpha, transformOrigin ->
            View(
                render = render,
                view = itemView,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.transformOrigin = transformOrigin
                        this.alpha = alpha
                    }
                    .clickable(render) { item.onClick?.invoke() },
            )
        }
    } else {
        View(
            render = render,
            view = itemView,
            modifier = Modifier
                .fillMaxSize()
                .clickable(render) { item.onClick?.invoke() },
        )
    }
}