package wallapp.ui.content.wallpaper

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import wallapp.content.state.wallpaper.WallpaperDetailItem
import wallapp.content.state.wallpaper.WallpaperDetailViewSpec
import wallapp.content.state.wallpaper.WallpaperDetailViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.content.state.wallpaper.WallpaperShowcaseViewSpec
import wallapp.content.state.wallpaper.WallpaperShowcaseViewState
import wallapp.graphics.color
import wallapp.graphics.composeColor
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.pager.TransformPagerEdgeItems
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip
import wallapp.pixel.swipetodismiss.SwipeToDismiss
import wallapp.pixel.text.Text
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.ThemeColors
import wallapp.pixel.theme.dynamicColorSchemeAndDarkStatusBarIcons
import wallapp.pixel.view.onClick
import wallapp.ui.AppScreen
import wallapp.ui.content.error.ErrorScreen
import wallapp.ui.content.loading.ContentWithLoading


@Composable
fun WallpaperShowcase(
    render: Render,
    viewState: WallpaperShowcaseViewState,
    modifier: Modifier = Modifier,
) {
    Surface {
        ContentWithLoading(
            render,
            loadingIsVisible = viewState is WallpaperShowcaseViewState.Loading,
            modifier,
        ) {contentModifier ->
            when (viewState) {
                is WallpaperShowcaseViewState.Success -> {
                    WallpaperShowcaseSuccess(render, viewState, contentModifier)
                }
                is WallpaperShowcaseViewState.Error -> {
                    ErrorScreen(render, contentModifier)
                }
                is WallpaperShowcaseViewState.Loading -> { }
            }
        }
    }
}

@Composable
fun WallpaperShowcaseSuccess(
    render: Render,
    viewState: WallpaperShowcaseViewState.Success,
    modifier: Modifier = Modifier,
) {
    val themeColors = viewState.themeColors

    if (themeColors != null) {
        WallpaperShowcaseSuccessCustomTheme(
            themeColors,
            render,
            modifier,
            viewState
        )
    } else {
        WallpaperShowcaseSuccess(
            render,
            modifier,
            viewState,
        )
    }
}

@Composable
private fun WallpaperShowcaseSuccessCustomTheme(
    themeColors: ThemeColors,
    render: Render,
    modifier: Modifier,
    viewState: WallpaperShowcaseViewState.Success,
) {
    val backgroundColor: Color by animateColorAsState(themeColors.background.composeColor)
    val primaryColor: Color by animateColorAsState(themeColors.primary.composeColor)

    val (colorScheme, darkStatusBarIcons) = dynamicColorSchemeAndDarkStatusBarIcons(
        themeColors.copy(
            background = backgroundColor.color,
            primary = primaryColor.color,
        ),
    )
    render.windowManager.setStatusBarDarkIcons(darkIcons = darkStatusBarIcons)

    AppTheme(
        render,
        colorScheme,
        shapes = MaterialTheme.shapes,
    ) {
        WallpaperShowcaseSuccess(
            render,
            modifier,
            viewState,
        )
    }
}

@Composable
fun WallpaperShowcaseSuccess(
    render: Render,
    modifier: Modifier,
    viewState: WallpaperShowcaseViewState.Success,
) {
    Scaffold {
        WallpaperShowcase(render, viewState, modifier)
    }
}

@Composable
fun WallpaperShowcase(
    render: Render,
    viewState: WallpaperShowcaseViewState.Success,
    modifier: Modifier = Modifier,
) {
    val overlayScreen = viewState.overlayScreen
    val shadowStatusBar = viewState.shadowStatusBar

    SwipeToDismiss(
        onSwipeToDismiss = viewState.onSwipeToDismiss,
        modifier = modifier
            .fillMaxHeight(),
    ) {
        WallpaperShowcase(
            render,
            viewState,
            viewState.viewSpec,
            Modifier
        )

        shadowStatusBar?.also {
            Image(render, it)
        }

        TopControlButtons(render, viewState)

        if (overlayScreen != null) {
            AppScreen(
                render = render,
                screenViewState = overlayScreen,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BoxScope.TopControlButtons(
    render: Render,
    viewState: WallpaperShowcaseViewState.Success,
) {
    val viewSpec = viewState.viewSpec
    val topControlButtonsVerticalOffset = viewSpec.topControlButtonsVerticalOffset

    MenuItem(
        render,
        viewState.close,
        modifier = Modifier
            .padding(top = topControlButtonsVerticalOffset)
            .align(Alignment.TopStart)
    )

    MenuItem(
        render,
        viewState.actionItems,
        modifier = Modifier
            .padding(top = topControlButtonsVerticalOffset)
            .align(Alignment.TopEnd),
    )
}

@Composable
fun WallpaperShowcase(
    render: Render,
    viewState: WallpaperShowcaseViewState.Success,
    viewSpec: WallpaperShowcaseViewSpec,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val bottomPadding = viewSpec.bottomPadding
    val backgroundColor = MaterialTheme.colorScheme.background
    val title = viewState.title

    val wallpaperArtist = viewState.wallpaperArtist
    val actionButton = viewState.actionButton
    val wallpaperDetail = viewState.detail

    val titleSpacing = viewSpec.maxTitleSpacing
    val minTitleSpacing = 0.dp
    val maxArtistTopSpacing = viewSpec.maxArtistTopSpacing
    val minArtistTopSpacing = viewSpec.minArtistTopSpacing
    val maxArtistBottomSpacing = viewSpec.maxArtistBottomSpacing
    val minArtistBottomSpacing = viewSpec.minArtistBottomSpacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WallpaperPreviewContent(
            render = render,
            viewState = viewState,
            viewSpec = viewSpec,
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = minTitleSpacing, max = titleSpacing)
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MenuItem(render, title)
        }

        Spacer(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = minTitleSpacing, max = titleSpacing)
        )

        WallpaperActionButton(render, viewSpec, actionButton)

        Spacer(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = minArtistTopSpacing, max = maxArtistTopSpacing)
        )

        WallpaperArtist(
            render,
            wallpaperArtist,
            modifier = Modifier
                .padding(
                    start = viewSpec.horizontalPadding,
                    end = viewSpec.horizontalPadding - 12.dp,
                ),
        )

        Spacer(
            modifier = Modifier
                .weight(0.8f)
                .heightIn(min = minArtistBottomSpacing, max = maxArtistBottomSpacing)
        )

        WallpaperDetails(
            render,
            wallpaperDetail,
            modifier = Modifier
                .padding(horizontal = viewSpec.horizontalPadding),
        )

        Spacer(modifier = Modifier.weight(1f))

        Spacer(
            modifier = Modifier
                .height(bottomPadding)
                .fillMaxWidth(),
        )
    }
}

@Composable
fun WallpaperDetails(
    render: Render,
    viewState: WallpaperDetailViewState,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val detailItems = viewState.detailItems ?: return

    WallpaperDetails(
        render,
        viewSpec,
        detailItems,
        modifier,
    )
}

@Composable
fun WallpaperDetails(
    render: Render,
    viewSpec: WallpaperDetailViewSpec,
    wallpaperDetails: List<WallpaperDetailItem>,
    modifier: Modifier = Modifier,
) {
    val height = viewSpec.height
    val spacerHeight = viewSpec.itemVerticalSpacerHeight

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        wallpaperDetails.forEachIndexed { index, wallpaperDetailItem ->
            WallpaperDetailItem(
                render,
                viewSpec,
                wallpaperDetailItem,
                modifier = Modifier.fillMaxWidth(),
            )
            if (index < wallpaperDetails.size - 1) {
                Spacer(modifier = Modifier.height(spacerHeight))
            }
        }
    }
}

@Composable
fun WallpaperDetailItem(
    render: Render,
    viewSpec: WallpaperDetailViewSpec,
    viewState: WallpaperDetailItem,
    modifier: Modifier = Modifier,
) {
    val image = viewState.image
    val label = viewState.label
    val itemHeight = viewSpec.itemHeight
    val itemImageSize = viewSpec.itemImageSize

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            render = render,
            image = image,
            modifier = Modifier.size(itemImageSize),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Composable
fun WallpaperPreview(
    render: Render,
    viewState: WallpaperShowcaseViewState.Success,
    viewSpec: WallpaperShowcaseViewSpec,
) {
    val wallpaperPreviews = viewState.wallpaperPreviews
    val firstWallpaperPreview = wallpaperPreviews.first()
    val imageHeight = firstWallpaperPreview.imageViewState.viewSpec.height
    val modifier = Modifier.fillMaxWidth()
        .height(imageHeight)

    if (wallpaperPreviews.size == 1) {
        WallpaperPreview(
            render = render,
            viewSpec = viewSpec,
            wallpaperPreview = firstWallpaperPreview,
            modifier = modifier,
        )
    } else {
        WallpaperPreview(
            render = render,
            viewSpec = viewSpec,
            wallpaperPreviews = wallpaperPreviews,
            viewState,
            modifier = modifier,
        )
    }
}

@Composable
fun WallpaperPreviewContent(
    render: Render,
    viewState: WallpaperShowcaseViewState.Success,
    viewSpec: WallpaperShowcaseViewSpec,
) {
    val wallpaperPreviews = viewState.wallpaperPreviews
    val firstWallpaperPreview = wallpaperPreviews.first()
    val imageHeight = firstWallpaperPreview.imageViewState.viewSpec.height
    val modifier = Modifier.fillMaxWidth()
        .height(imageHeight)

    if (wallpaperPreviews.size == 1) {
        WallpaperPreviewContent(
            render = render,
            viewSpec = viewSpec,
            wallpaperPreview = firstWallpaperPreview,
            modifier = modifier,
        )
    } else {
        WallpaperPreviewContent(
            render = render,
            viewSpec = viewSpec,
            wallpaperPreviews = wallpaperPreviews,
            viewState,
            modifier = modifier,
        )
    }
}

@Composable
private fun WallpaperPreview(
    render: Render,
    viewSpec: WallpaperShowcaseViewSpec,
    wallpaperPreview: WallpaperPreviewViewState,
    modifier: Modifier = Modifier,
) {
    val horizontalPadding = viewSpec.horizontalPadding

    Box(modifier = modifier) {
        WallpaperPreviewContent(
            render,
            wallpaperPreview,
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
        )
    }
}
@Composable
private fun WallpaperPreviewContent(
    render: Render,
    viewSpec: WallpaperShowcaseViewSpec,
    wallpaperPreview: WallpaperPreviewViewState,
    modifier: Modifier = Modifier,
) {

    Box(modifier = modifier.clip(viewSpec.wallpaperPreviewShapeSpec, render.shapeClipper)) {
        WallpaperPreviewContent(
            render,
            wallpaperPreview,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WallpaperPreview(
    render: Render,
    viewSpec: WallpaperShowcaseViewSpec,
    wallpaperPreviews: List<WallpaperPreviewViewState>,
    viewState: WallpaperShowcaseViewState.Success,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val onPageChangedWallpaperPreview = viewState.onPageChangedWallpaperPreview
    val currentPage = viewState.currentPreviewIndex!!
    val pageCount = wallpaperPreviews.size
    val itemSpacing = viewSpec.previewImagePageSpacing

    val pagerState = rememberPagerState(initialPage = currentPage) { pageCount }
    val scope = rememberCoroutineScope()

    val indicatorScrollState = rememberLazyListState()

    LaunchedEffect(key1 = pagerState.currentPage, block = {
        val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
        val lastVisibleIndex =
            indicatorScrollState.layoutInfo.visibleItemsInfo.last().index
        val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex

        if (currentPage > lastVisibleIndex - 1) {
            indicatorScrollState.animateScrollToItem(currentPage - size + 2)
        } else if (currentPage <= firstVisibleItemIndex + 1) {
            val targetPage = if (currentPage - 1 >= 0) currentPage - 1 else 0
            indicatorScrollState.animateScrollToItem(targetPage)
        }
    })

    val onPreviousClick = {
        scope.launch {
            val previousPage = pagerState.currentPage - 1
            if (previousPage >= 0) {
                pagerState.animateScrollToPage(previousPage)
            }
        }
    }
    val onNextClick = {
        scope.launch {
            val nextPage = pagerState.currentPage + 1
            if (nextPage < wallpaperPreviews.size) {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(modifier = modifier) {
        WallpaperPreviewPager(
            render,
            modifier,
            viewSpec,
            pagerState,
            wallpaperPreviews,
            onPageChangedWallpaperPreview,
        )

        PageIndicators(
            render,
            indicatorScrollState,
            pagerState,
            pageCount,
            viewSpec,
            backgroundColor = viewState.indicatorPillBackgroundColor.composeColor,
            indicatorColor = viewState.indicatorColor.composeColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingDefault),
        )

        Box(
            modifier = Modifier
                .width(itemSpacing)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .semantics { contentDescription = viewState.previousClickContentDescription}
                .clickableNoRipple { onPreviousClick.invoke() }
        )

        Box(
            modifier = Modifier
                .width(itemSpacing)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .semantics { contentDescription = viewState.nextClickContentDescription}
                .clickableNoRipple { onNextClick.invoke() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WallpaperPreviewContent(
    render: Render,
    viewSpec: WallpaperShowcaseViewSpec,
    wallpaperPreviews: List<WallpaperPreviewViewState>,
    viewState: WallpaperShowcaseViewState.Success,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val onPageChangedWallpaperPreview = viewState.onPageChangedWallpaperPreview
    val currentPage = viewState.currentPreviewIndex!!
    val pageCount = wallpaperPreviews.size
    val itemSpacing = viewSpec.previewImagePageSpacing
    val previewActionButton = viewState.previewActionButton

    val pagerState = rememberPagerState(initialPage = currentPage) { pageCount }
    val scope = rememberCoroutineScope()

    val indicatorScrollState = rememberLazyListState()

    LaunchedEffect(key1 = pagerState.currentPage, block = {
        val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
        val lastVisibleIndex =
            indicatorScrollState.layoutInfo.visibleItemsInfo.last().index
        val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex

        if (currentPage > lastVisibleIndex - 1) {
            indicatorScrollState.animateScrollToItem(currentPage - size + 2)
        } else if (currentPage <= firstVisibleItemIndex + 1) {
            val targetPage = if (currentPage - 1 >= 0) currentPage - 1 else 0
            indicatorScrollState.animateScrollToItem(targetPage)
        }
    })

    val onPreviousClick = {
        scope.launch {
            val previousPage = pagerState.currentPage - 1
            if (previousPage >= 0) {
                pagerState.animateScrollToPage(previousPage)
            }
        }
    }
    val onNextClick = {
        scope.launch {
            val nextPage = pagerState.currentPage + 1
            if (nextPage < wallpaperPreviews.size) {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(modifier = modifier.clip(viewSpec.wallpaperPreviewShapeSpec, render.shapeClipper)) {
        WallpaperPreviewPagerContent(
            render,
            modifier,
            viewSpec,
            pagerState,
            wallpaperPreviews,
            onPageChangedWallpaperPreview,
        )

        PageIndicators(
            render,
            indicatorScrollState,
            pagerState,
            pageCount,
            viewSpec,
            backgroundColor = viewState.indicatorPillBackgroundColor.composeColor,
            indicatorColor = viewState.indicatorColor.composeColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingDefault),
        )

        if (previewActionButton != null) {
            MenuItem(
                render,
                previewActionButton,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = paddingDefault, end = paddingDefault),
            )
        }

        Box(
            modifier = Modifier
                .width(itemSpacing)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .semantics { contentDescription = viewState.previousClickContentDescription }
                .clickableNoRipple { onPreviousClick.invoke() }
        )

        Box(
            modifier = Modifier
                .width(itemSpacing)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .semantics { contentDescription = viewState.nextClickContentDescription }
                .clickableNoRipple { onNextClick.invoke() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperPreviewPager(
    render: Render,
    modifier: Modifier,
    viewSpec: WallpaperShowcaseViewSpec,
    pagerState: PagerState,
    wallpaperPreviews: List<WallpaperPreviewViewState>,
    onPageChangedWallpaperPreview: (Int) -> Unit,
) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChangedWallpaperPreview.invoke(page)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = viewSpec.horizontalPadding),
        pageSpacing = viewSpec.previewImagePageSpacing,
    ) { page ->
        val wallpaperPreview = wallpaperPreviews[page]

        TransformPagerEdgeItems(
            page = page,
            pagerState = pagerState,
            itemsSize = wallpaperPreviews.size,
            minScale = .9f,
        ) { scale, alpha, transformOrigin ->
            WallpaperPreviewContent(
                render,
                wallpaperPreview,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.transformOrigin = transformOrigin
                        this.alpha = alpha
                    }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperPreviewPagerContent(
    render: Render,
    modifier: Modifier,
    viewSpec: WallpaperShowcaseViewSpec,
    pagerState: PagerState,
    wallpaperPreviews: List<WallpaperPreviewViewState>,
    onPageChangedWallpaperPreview: (Int) -> Unit,
) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChangedWallpaperPreview.invoke(page)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { page ->
        val wallpaperPreview = wallpaperPreviews[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            WallpaperPreviewContent(
                render,
                wallpaperPreview,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun WallpaperActionButton(
    render: Render,
    viewSpec: WallpaperShowcaseViewSpec,
    actionButton: MenuItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = viewSpec.horizontalPadding),
    ) {

        MenuItem(
            render,
            menuItem = actionButton,
            modifier = Modifier
                .weight(1f),
        )
    }
}


@Composable
private fun WallpaperPreviewContent(
    render: Render,
    wallpaperPreview: WallpaperPreviewViewState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        WallpaperPreviewImage(render, wallpaperPreview)
    }
}

@Composable
private fun WallpaperPreviewImage(
    render: Render,
    wallpaperPreview: WallpaperPreviewViewState,
) {
    val imageViewState = wallpaperPreview.imageViewState
    val onClick = wallpaperPreview.onClick.onClick
    val viewSpec = wallpaperPreview.viewSpec

    Image(
        render,
        viewState = imageViewState,
        modifier = Modifier
            .width(viewSpec.width)
            .height(viewSpec.height)
            .clip(viewSpec.shapeSpec, render.shapeClipper)
            .semantics {
                contentDescription = wallpaperPreview.title?.string.orEmpty()
            }
            .clickable(render, onClick),
        animateLoading = true,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageIndicators(
    render: Render,
    indicatorScrollState: LazyListState,
    pagerState: PagerState,
    pageCount: Int,
    viewSpec: WallpaperShowcaseViewSpec,
    backgroundColor: Color,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    val height = viewSpec.indicatorContainerHeight
    val cornerRounding = height / 2
    val paddingSmall = render.defaultViewSpec.paddingSmall

    LazyRow(
        state = indicatorScrollState,
        modifier = modifier
            .height(height)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRounding)
            ),
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
