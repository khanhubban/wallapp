package wallapp.ui.content.highlightcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import wallapp.content.state.carousel.CarouselPageViewState
import wallapp.content.state.carousel.CarouselViewSpec
import wallapp.content.state.exhibit.ExhibitViewState
import wallapp.content.state.explore.HighlightCarouselViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.feed.LocalFeedContentNestedScrollController
import wallapp.pixel.input.NestedScrollConsumingFeedController
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.DefaultViewSpecPreset.paddingDefault
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HighlightsCarousel(
    render: Render,
    viewState: HighlightCarouselViewState,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.carouselViewSpec
    val (extendedHeight, extraHeightForBottom, offsetY) = calculateCarouselOffsets(viewSpec)
    val offsetYScaled = offsetY / 1.5f

    val onPageChange = viewState.onPageChange
    val pageCount = viewState.carouselViewState.pages.size
    val initialPage = viewState.carouselViewState.initialPage

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f,
    ) {
        pageCount
    }

    val indicatorScrollState = rememberLazyListState()

    val autoScrollFirstDelayInSeconds = viewState.autoScrollFirstDelayInSeconds
    val autoScrollDelayInSeconds = viewState.autoScrollDelayInSeconds

    // Auto-scroll variables
    var autoScrollInterval = remember { autoScrollFirstDelayInSeconds * 1000L }
    val autoScrollDirection = remember { mutableStateOf(1) } // 1 for forward, -1 for backward
    val isAutoScrolling = remember { mutableStateOf(false) }
    val hasUserInteracted = remember { mutableStateOf(false) }

    val isUserInteracting by remember {
        derivedStateOf {
            pagerState.isScrollInProgress && !isAutoScrolling.value
        }
    }

    // Track if user manually interacts with the pager and disable auto-scroll if they do
    LaunchedEffect(isUserInteracting) {
        if (isUserInteracting) {
            hasUserInteracted.value = true
        }
    }

    // Auto-scroll coroutine
    LaunchedEffect(Unit) {
        while (true) {
            delay(autoScrollInterval)
            if (!isUserInteracting && pageCount > 1 && !hasUserInteracted.value) {
                autoScrollInterval = autoScrollDelayInSeconds * 1000L
                var nextPage = pagerState.currentPage + autoScrollDirection.value
                if (nextPage >= pageCount) {
                    autoScrollDirection.value = -1
                    nextPage = pagerState.currentPage + autoScrollDirection.value
                } else if (nextPage < 0) {
                    autoScrollDirection.value = 1
                    nextPage = pagerState.currentPage + autoScrollDirection.value
                }
                isAutoScrolling.value = true
                pagerState.animateScrollToPage(nextPage)
                isAutoScrolling.value = false
            }
        }
    }

    // Update the indicator when the current page changes
    LaunchedEffect(
        key1 = pagerState.currentPage,
        key2 = indicatorScrollState.layoutInfo.visibleItemsInfo.size,
    ) {
        onPageChange(pagerState.currentPage)

        // Existing logic for updating indicatorScrollState
        val items = indicatorScrollState.layoutInfo.visibleItemsInfo
        if (items.isNotEmpty()) {
            val size = items.size
            val lastVisibleIndex = items.last().index
            val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex

            if (pagerState.currentPage > lastVisibleIndex - 1) {
                indicatorScrollState.animateScrollToItem(pagerState.currentPage - size + 2)
            } else if (pagerState.currentPage <= firstVisibleItemIndex + 1) {
                val targetPage = if (pagerState.currentPage - 1 >= 0) pagerState.currentPage - 1 else 0
                indicatorScrollState.animateScrollToItem(targetPage)
            }
        }
    }

    // Calculate label and indicator alpha
    val alpha = calculateAlpha(viewSpec.height, offsetY)

    val indicatorHeight = viewSpec.indicatorContainerHeight
    val labelBottomPadding = extraHeightForBottom + indicatorHeight + (paddingDefault * 2)

    Box(
        modifier = modifier
            .height(extendedHeight)
            .background(ThemeColorTypeMapper.map(viewState.backgroundColor))
            .offset(y = -extraHeightForBottom)
    ) {
        HighlightsCarouselPagerContent(
            render,
            modifier = Modifier
                .offset { IntOffset(0, offsetYScaled.roundToInt()) },
            pagerState = pagerState,
            highlights = viewState.carouselViewState.pages,
            shadowBottomPadding = extraHeightForBottom - paddingDefault, // slightly less to accommodate rounded corners of feed
            labelBottomPadding = labelBottomPadding,
            bottomOffsetY = (offsetY - offsetYScaled).roundToInt(),
            labelAlpha = alpha,
        )

        HighlightsCarouselPageIndicators(
            render,
            indicatorScrollState,
            pagerState,
            pageCount,
            indicatorColor = Color.White,
            viewSpec,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingDefault)
                .offset(y = -extraHeightForBottom)
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .alpha(alpha),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HighlightsCarouselPagerContent(
    render: Render,
    modifier: Modifier,
    pagerState: PagerState,
    highlights: List<CarouselPageViewState>,
    shadowBottomPadding: Dp,
    labelBottomPadding: Dp,
    bottomOffsetY: Int,
    labelAlpha: Float,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { page ->
        val highlight = highlights[page]
        val exhibitViewState = highlight.view.viewState as? ExhibitViewState ?: return@HorizontalPager
        val image = exhibitViewState.imageViewState
        val label = exhibitViewState.label
        val additionalLabel = exhibitViewState.additionalLabel
        val bottomShadowImage = exhibitViewState.bottomShadowImage

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(render) { highlight.onClick?.invoke() }
        ) {
            Image(
                render = render,
                viewState = image,
                modifier = Modifier.fillMaxSize(),
            )

            Image(
                render = render,
                viewState = bottomShadowImage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -shadowBottomPadding)
                    .offset { IntOffset(0, bottomOffsetY) }
                    .rotate(180f),
            )

            if (label != null || additionalLabel != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -labelBottomPadding)
                        .offset { IntOffset(0, bottomOffsetY) }
                        .alpha(labelAlpha),
                ) {
                    label?.also {
                        Text(
                            text = label,
                            colorOverride = Color.White,
                        )
                    }

                    additionalLabel?.also {
                        if (label != null) {
                            Spacer(modifier = Modifier.height(paddingSmall))
                        }
                        Text(
                            text = additionalLabel,
                            colorOverride = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HighlightsCarouselPageIndicators(
    render: Render,
    indicatorScrollState: LazyListState,
    pagerState: PagerState,
    pageCount: Int,
    indicatorColor: Color,
    viewSpec: CarouselViewSpec,
    modifier: Modifier = Modifier,
) {
    val height = viewSpec.indicatorContainerHeight
    val paddingSmall = render.defaultViewSpec.paddingSmall

    LazyRow(
        state = indicatorScrollState,
        modifier = modifier
            .height(height),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = paddingSmall),
    ) {
        repeat(pageCount) { iteration ->
            item(key = "item$iteration") {
                Box(
                    modifier = Modifier
                        .padding(viewSpec.indicatorSpacing)
                        .background(
                            color = indicatorColor.copy(alpha = if (pagerState.currentPage == iteration) 1f else 0.4f),
                            shape = CircleShape
                        )
                        .size(viewSpec.indicatorSize)
                )
            }
        }
    }
}

@Composable
private fun calculateCarouselOffsets(
    viewSpec: CarouselViewSpec,
): Triple<Dp, Dp, Float> {
    val height = viewSpec.height
    val heightPx = with(LocalDensity.current) { height.roundToPx().toFloat() }
    val feedScrollController: NestedScrollConsumingFeedController? =
        LocalFeedContentNestedScrollController.current
    val feedYOffset = (feedScrollController?.currentYOffsetPx ?: 0f)
    val offsetY = (feedYOffset - heightPx).coerceIn(-heightPx, 0f)
    val extraHeight = viewSpec.extraHeightForScroll
    val finalHeight = height + extraHeight
    return Triple(finalHeight, extraHeight / 2, offsetY)
}

@Composable
private fun calculateAlpha(
    height: Dp,
    offsetY: Float,
): Float {
    var alpha = 1f
    val heightPx = with(LocalDensity.current) { height.roundToPx().toFloat() }
    val alphaThreshold = heightPx / 3
    val absOffsetY = abs(offsetY)
    if (absOffsetY > alphaThreshold) {
        val progress = (absOffsetY - alphaThreshold) / alphaThreshold
        alpha = 1 - progress
    }
    return alpha
}