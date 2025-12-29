package wallapp.ui.content.index

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.index.IndexViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.log.Log
import wallapp.pixel.feed.LocalExploreFeedToolbarNestedScrollController
import wallapp.pixel.feed.LocalFeedContentNestedScrollController
import wallapp.pixel.feed.LocalFeedToolbarNestedScrollController
import wallapp.pixel.input.NestedScrollConsumingFeedController
import wallapp.pixel.input.NestedScrollController
import wallapp.pixel.input.rememberNestedScrollConnectionScrollableContainers
import wallapp.pixel.input.rememberNestedScrollController
import wallapp.pixel.input.rememberNestedScrollExploreToolbarController
import wallapp.pixel.navigationbar.NavigationBar
import wallapp.pixel.pager.pagerState
import wallapp.pixel.render.Render
import wallapp.ui.AppScreen
import wallapp.ui.content.search.SearchInputOverlay
import kotlin.math.roundToInt


@Composable
fun IndexScreen(
    render: Render,
    viewState: IndexViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is IndexViewState.Loading -> { }
        is IndexViewState.Success -> {
            IndexScreen(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun IndexScreen(
    render: Render,
    viewState: IndexViewState.Success,
    modifier: Modifier = Modifier,
) {
    val navigationBarViewState = viewState.navigationBar

    val consumingController = rememberNestedScrollConsumingFeedController(viewState)

    val statusBarHeight = render.windowFrame.statusBarHeight
    val statusBarHeightPx = with(LocalDensity.current) { statusBarHeight.roundToPx().toFloat() }
    val toolbarHeight = viewState.scrollableTopBarHeight?.dp
    val maxToolbarHeightPx = toolbarHeight
        ?.let { with(LocalDensity.current) { (toolbarHeight + statusBarHeight).roundToPx().toFloat() } }
    val currentTopBarYOffsetPx = remember(maxToolbarHeightPx) { Animatable(0f) }
    val topBarScrollController: NestedScrollController? = maxToolbarHeightPx?.let {
        rememberNestedScrollController(
            currentYOffsetPx = currentTopBarYOffsetPx,
            maxHeightPx = maxToolbarHeightPx,
        )
    }

    val currentExploreToolbarYOffsetPx = consumingController?.let { remember { mutableStateOf(it.currentYOffsetPx) } }
    val exploreToolbarScrollController: NestedScrollController? = if (currentExploreToolbarYOffsetPx != null && maxToolbarHeightPx != null) {
        rememberNestedScrollExploreToolbarController(
            currentYOffsetPx = currentExploreToolbarYOffsetPx,
            maxHeightPx = maxToolbarHeightPx,
            topStickyOffset = statusBarHeightPx,
            consumingController = consumingController,
        )
    } else {
        null
    }

    val systemNavigationBarHeightOffset = navigationBarViewState.viewSpec.bottomNavBarItemsYOffset
    val navigationBarHeight = navigationBarViewState.viewSpec.navBarItemsHeight +
            systemNavigationBarHeightOffset
    val maxNavigationBarHeightPx = with(LocalDensity.current) { navigationBarHeight.roundToPx().toFloat() }
    val currentNavigationBarYOffsetPx = remember { Animatable(0f) }
    val navigationBarScrollController: NestedScrollController =
        rememberNestedScrollController(
            currentYOffsetPx = currentNavigationBarYOffsetPx,
            maxHeightPx = maxNavigationBarHeightPx,
        )

    val usePager = viewState.usePager
    val navigationBarForceShow = navigationBarViewState.forceShow
    Log.d("navigationBarForceShow: $navigationBarForceShow")
    val navigationBarForceShowState = remember { mutableStateOf(navigationBarForceShow) }
    LaunchedEffect(navigationBarForceShow) {
        navigationBarForceShowState.value = navigationBarForceShow
    }
    LaunchedEffect(navigationBarForceShowState.value) {
        when (navigationBarForceShowState.value) {
            true -> navigationBarScrollController.hide()
            false -> navigationBarScrollController.show()
            else -> {}
        }
    }

    val nestedScrollConnection: NestedScrollConnection =
        rememberNestedScrollConnectionScrollableContainers(
            listOfNotNull(exploreToolbarScrollController, topBarScrollController, navigationBarScrollController),
            consumingController = consumingController,
        )

    Surface(modifier = modifier) {
        Scaffold(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            bottomBar = {
                NavigationBar(
                    render = render,
                    viewState = navigationBarViewState,
                    systemNavigationBarHeightOffset = systemNavigationBarHeightOffset,
                    modifier = Modifier
                        .height(navigationBarHeight)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -navigationBarScrollController.currentYOffsetPx.roundToInt()
                            )
                        },
                )
            }
        ) {
            // topBarScrollController applied deeper in the render stack
            CompositionLocalProvider(
                LocalExploreFeedToolbarNestedScrollController provides exploreToolbarScrollController,
                LocalFeedToolbarNestedScrollController provides topBarScrollController,
                LocalFeedContentNestedScrollController provides consumingController,
            ) {
                IndexScreen(
                    render = render,
                    viewState = viewState,
                    usePager = usePager,
                )
            }
        }
    }
}

@Composable
private fun rememberNestedScrollConsumingFeedController(viewState: IndexViewState.Success): NestedScrollConsumingFeedController? {
    val exploreViewState = ((viewState.currentScreenViewState as? ExploreViewState) as? ExploreViewState.Success) ?: return null
    val viewSpec = exploreViewState.highlightCarouselViewState?.carouselViewSpec ?: return null
    val highlightCarouselHeight = viewSpec.height
    val highlightsHeightPx = with(LocalDensity.current) {
        highlightCarouselHeight.roundToPx().toFloat()
    }
    val currentHighlightsYOffsetPx = remember { mutableStateOf(exploreViewState.feedOffset ?: highlightsHeightPx) }
    val consumingController = remember {
        NestedScrollConsumingFeedController(currentHighlightsYOffsetPx, highlightsHeightPx)
    }
    return consumingController
}

@Composable
fun IndexScreen(
    render: Render,
    viewState: IndexViewState.Success,
    usePager: Boolean,
    modifier: Modifier = Modifier,
) {
    val overlay = viewState.overlayScreen
    val overlayVisible = viewState.overlayVisible

    var overlayVisibilityProgress by remember { mutableStateOf(0f) }
    val onOverlayVisibilityProgress = remember {
        { alpha: Float ->
            overlayVisibilityProgress = alpha
            viewState.onOverlayVisibilityProgress(alpha)
        }
    }

    Box(
        modifier = modifier,
    ) {
        IndexScreenContent(render, viewState, usePager, Modifier.fillMaxSize())

        if ((overlayVisible || overlayVisibilityProgress > 0f) && overlay is SearchInputViewState) {
            Box(modifier = Modifier.fillMaxSize()) {
                SearchInputOverlay(
                    render = render,
                    viewState = overlay,
                    overlayVisible = overlayVisible,
                    onOverlayDismissed = viewState.onOverlayDismissed,
                    onOverlayVisibilityProgress = onOverlayVisibilityProgress,
                    overlayVisibilityProgress = overlayVisibilityProgress,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun IndexScreenContent(
    render: Render,
    viewState: IndexViewState.Success,
    usePager: Boolean,
    modifier: Modifier = Modifier,
) {
    if (usePager) {
        IndexScreenContentPager(render, viewState, modifier)
    } else {
        IndexScreenContent(render, viewState, modifier)
//        IndexScreenContentCrossfade(render, viewState, modifier)
    }
}

@Composable
fun IndexScreenContent(
    render: Render,
    viewState: IndexViewState.Success,
    modifier: Modifier = Modifier,
) {
    val currentScreen = viewState.currentScreenViewState

    AppScreen(
        render = render,
        screenViewState = currentScreen,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndexScreenContentPager(
    render: Render,
    viewState: IndexViewState.Success,
    modifier: Modifier = Modifier,
) {
    val screens = viewState.screens
    val currentScreenIndex = viewState.currentScreenIndex
    require(currentScreenIndex < screens.size) {
        "Data error: initialPage: $currentScreenIndex, screenCount: ${screens.size}"
    }

    val scope = rememberCoroutineScope()
    val pagerState: PagerState = rememberPagerState(initialPage = currentScreenIndex) { screens.size }

    viewState.onPagerStateChange.invoke(pagerState.pagerState)

    LaunchedEffect(key1 = currentScreenIndex) {
        scope.launch {
            pagerState.animateScrollToPage(currentScreenIndex)
        }
    }

    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        userScrollEnabled = true,
        state = pagerState,
    ) { page ->
        val screen = screens[page]
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            AppScreen(
                render = render,
                screenViewState = screen,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun IndexScreenContentCrossfade(
    render: Render,
    viewState: IndexViewState.Success,
    modifier: Modifier = Modifier,
) {
    val currentScreen = viewState.currentScreenViewState
    val crossfadeDuration = viewState.screenCrossfadeDuration.inWholeMilliseconds.toInt()

    // We only want to perform a crossfade upon a screen instance change, not a data change.
    val targetState = currentScreen::class.simpleName

    Crossfade(
        targetState = targetState,
        animationSpec = tween(durationMillis = crossfadeDuration),
    ) { _ ->
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            AppScreen(
                render = render,
                screenViewState = currentScreen,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
