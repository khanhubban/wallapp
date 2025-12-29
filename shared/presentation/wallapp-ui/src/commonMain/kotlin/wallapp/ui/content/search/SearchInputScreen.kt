package wallapp.ui.content.search

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.content.state.search.SearchBarViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.image.Image
import wallapp.math.clamp
import wallapp.pixel.compose.clickableIfNotNull
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.pxToDp
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.view.onClick
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SearchInputScreen(
    render: Render,
    viewState: SearchInputViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is SearchInputViewState.Loading -> { }

        is SearchInputViewState.Data -> {
            SearchInputScreen(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun SearchInputScreen(
    render: Render,
    viewState: SearchInputViewState.Data,
    modifier: Modifier = Modifier,
) {
    if (viewState.isOverlay) {
        SearchInputOverlay(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    } else {
        SearchInputContent(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    }
}

@Composable
fun SearchInputOverlay(
    render: Render,
    viewState: SearchInputViewState,
    overlayVisible: Boolean = false,
    onOverlayDismissed: () -> Unit = {},
    onOverlayVisibilityProgress: (Float) -> Unit = {},
    overlayVisibilityProgress: Float = 0f,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        SearchInputViewState.Loading -> { }

        is SearchInputViewState.Data -> {
            SearchInputOverlay(
                render = render,
                viewState = viewState,
                overlayVisible = overlayVisible,
                onOverlayDismissed = onOverlayDismissed,
                onOverlayVisibilityProgress = onOverlayVisibilityProgress,
                overlayVisibilityProgress = overlayVisibilityProgress,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SearchInputOverlay(
    render: Render,
    viewState: SearchInputViewState.Data,
    overlayVisible: Boolean = false,
    onOverlayDismissed: () -> Unit = {},
    onOverlayVisibilityProgress: (Float) -> Unit = {},
    overlayVisibilityProgress: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val onScrimClick = viewState.onScrimClick ?: return
    val scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = .3f)
    val contentBackground = MaterialTheme.colorScheme.surface

    val expandedHeightPx: MutableState<Int?> = remember { mutableStateOf(null) }
    val containerShape = RoundedCornerShape(bottomStart = paddingDefault, bottomEnd = paddingDefault)

    val transitionEnabled = viewState.searchBar.transitionEnabled

    val hazeState = remember { HazeState() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .alpha(overlayVisibilityProgress)
                .fillMaxSize()
                .clickableIfNotNull(render, onScrimClick.onClick)
                .background(scrimColor)
        )
        DraggableSearchOverlay(
            expandedHeightPx = expandedHeightPx,
            overlayVisible = overlayVisible,
            onOverlayDismissed = onOverlayDismissed,
            onOverlayVisibilityProgress = onOverlayVisibilityProgress,
        ) {
            SearchInputContent(
                render = render,
                viewState = viewState,
                showSearchBar = !transitionEnabled,
                modifier = Modifier
                    .onGloballyPositioned {
                        if (expandedHeightPx.value == null) {
                            expandedHeightPx.value = it.size.height
                        }
                    }
                    .clip(shape = containerShape)
                    .background(contentBackground)
                    .haze(hazeState),
            )
        }

        val searchBarViewState = viewState.searchBar
        val viewSpec = viewState.viewSpec
        val searchBarHeight = viewSpec.searchBarHeight
        val searchFiltersTopPadding = viewSpec.searchFiltersTopPadding
        val topPadding = viewSpec.topPadding
        val searchBarHorizontalPadding = viewSpec.searchBarHorizontalPadding

        if (transitionEnabled) {
            HazeBlurLayer(
                hazeState,
                blurRadius = 8.dp,
                modifier = Modifier.fillMaxWidth()
                    .height(searchFiltersTopPadding)
            )

            SearchBar(
                render,
                searchBarViewState,
                searchBarHeight,
                searchFiltersTopPadding,
                searchBarHorizontalPadding,
                overlayVisibilityProgress,
                searchBarDisabled = false,
                modifier = Modifier.padding(top = topPadding),
            )
        }
    }
}

@Composable
fun SearchInputContent(
    render: Render,
    viewState: SearchInputViewState,
    modifier: Modifier = Modifier,
    showSearchBar: Boolean = true,
) {
    when (viewState) {
        is SearchInputViewState.Loading -> { }

        is SearchInputViewState.Data -> {
            SearchInputContent(
                render = render,
                viewState = viewState,
                modifier = modifier,
                showSearchBar = showSearchBar,
            )
        }
    }
}

@Composable
private fun SearchInputContent(
    render: Render,
    viewState: SearchInputViewState.Data,
    modifier: Modifier = Modifier,
    showSearchBar: Boolean = true,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault

    val viewSpec = viewState.viewSpec
    val searchFiltersTopPadding = viewSpec.searchFiltersTopPadding

    val footerContainerColor = MaterialTheme.colorScheme.background

    val sheetExpanded = viewState.searchSheetExpanded
    val sheetMinimizedHeight = searchFiltersTopPadding + paddingDefault

    val topPadding = viewSpec.topPadding

    val dropShadowImage = viewState.sheetDropShadow
    val dropShadowHeight = dropShadowImage.viewSpec.height

    val searchBarViewState = viewState.searchBar
    val searchBarHeight = viewSpec.searchBarHeight
    val searchBarHorizontalPadding = viewSpec.searchBarHorizontalPadding

    Column(verticalArrangement = Arrangement.spacedBy(-(dropShadowHeight - 32.dp))) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clickableNoRipple { }
                .animateContentSize()
                .height(
                    if (sheetExpanded) {
                        Dp.Unspecified
                    } else {
                        sheetMinimizedHeight
                    }
                )
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topPadding)
            )

            if (showSearchBar) {
                SearchBar(
                    render,
                    searchBarViewState,
                    searchBarHeight,
                    searchFiltersTopPadding,
                    searchBarHorizontalPadding,
                    searchBarDisabled = true, // disabling as we don't want this search bar to show the query, the query belongs to the search bar that transitions
                    overlayVisibilityProgress = 1f,
                )
            } else {
                Spacer(modifier = Modifier.height(searchFiltersTopPadding))
            }

            SearchFilters(
                render,
                viewState,
                footerContainerColor = footerContainerColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(),
            )

            SearchBottomSwipeIndicator(footerContainerColor)
        }

        // Drop shadow image
        val onDropShadowClick = viewState.onScrimClick
        Image(
            render = render,
            viewState = dropShadowImage,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(-1f)
                .clickableIfNotNull(render, onDropShadowClick!!.onClick),
        )
    }
}

@Composable
fun SearchBar(
    render: Render,
    searchBarViewState: SearchBarViewState,
    searchBarHeight: Dp,
    searchFiltersTopPadding: Dp,
    searchBarHorizontalPadding: Dp,
    overlayVisibilityProgress: Float,
    searchBarDisabled: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(searchFiltersTopPadding),
    ) {
        Spacer(modifier = Modifier.statusBarsPadding(render.windowFrame))

        Spacer(modifier = Modifier.weight(1f))

        SearchBar(
            render = render,
            viewState = searchBarViewState,
            height = searchBarHeight,
            overlayVisibilityProgress = overlayVisibilityProgress,
            searchBarDisabled = searchBarDisabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(searchBarHeight)
                .padding(horizontal = searchBarHorizontalPadding),
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SearchBottomSwipeIndicator(
    footerContainerColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(footerContainerColor),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 120.dp, height = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.4f)),
        )
    }
}

enum class SearchOverlayState {
    Hidden,
    Expanded,
}

@Composable
private fun DraggableSearchOverlay(
    expandedHeightPx: MutableState<Int?>,
    overlayVisible: Boolean,
    onOverlayDismissed: () -> Unit,
    onOverlayVisibilityProgress: (Float) -> Unit,
    content: @Composable (Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val overlayState = remember { mutableStateOf(SearchOverlayState.Hidden) }

    val translationYExpandedBound = 0f
    val translationYCollapsedBound = expandedHeightPx.value?.toFloat()?.unaryMinus()
    val translationY = remember(expandedHeightPx.value) {
        Animatable(translationYCollapsedBound ?: Float.MIN_VALUE).also {
            if (expandedHeightPx.value != null) {
                it.updateBounds(
                    lowerBound = translationYCollapsedBound,
                    upperBound = translationYExpandedBound,
                )
            }
        }
    }

    val draggableState = rememberDraggableState { dragAmount ->
        coroutineScope.launch {
            val newTranslateY = translationY.value + dragAmount
            translationY.snapTo(newTranslateY)
        }
    }

    val decay = rememberSplineBasedDecay<Float>()

    LaunchedEffect(overlayVisible, expandedHeightPx.value) {
        if (translationYCollapsedBound == null) return@LaunchedEffect
        if (overlayVisible) {
            translationY.animateTo(translationYExpandedBound, tween(350, easing = LinearOutSlowInEasing))
            overlayState.value = SearchOverlayState.Expanded
        } else if (!overlayVisible) {
            translationY.animateTo(translationYCollapsedBound, tween(300, easing = LinearOutSlowInEasing))
            overlayState.value = SearchOverlayState.Hidden
            onOverlayDismissed()
        }
    }

    val alpha = if (translationYCollapsedBound == null) 0f else {
        clamp(1 - (abs(translationY.value - translationYExpandedBound) /
                (translationYExpandedBound - translationYCollapsedBound)), 0f, 1f)
    }
    onOverlayVisibilityProgress(alpha)

    val onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = onDragStopped@{ velocity ->
        val height = expandedHeightPx.value?.toFloat() ?: return@onDragStopped
        val decayY = decay.calculateTargetValue(translationY.value, velocity)
        coroutineScope.launch {
            val targetY = if (decayY < -height * 0.5) -height else 0f
            if (targetY == -height) {
                overlayState.value = SearchOverlayState.Hidden
            } else {
                overlayState.value = SearchOverlayState.Expanded
            }
            val canReachTargetWithDecay = (decayY < targetY && targetY == -height) ||
                    (decayY > targetY && targetY == 0f)
            if (canReachTargetWithDecay) {
                translationY.animateDecay(initialVelocity = velocity, animationSpec = decay)
                // Ensure the overlay is off-screen before calling onOverlayDismissed
                if (targetY == -height) {
                    overlayState.value = SearchOverlayState.Hidden
                    onOverlayDismissed()
                }
            } else {
                translationY.animateTo(targetY, initialVelocity = velocity)
                // Ensure the overlay is off-screen before calling onOverlayDismissed
                if (targetY == -height) {
                    overlayState.value = SearchOverlayState.Hidden
                    onOverlayDismissed()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .alpha(alpha * 4)
            .offset(y = translationY.value.roundToInt().pxToDp())
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = onDragStopped,
            )
    ) {
        content(alpha)
    }
}