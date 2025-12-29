package wallapp.ui.content.carousel

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import wallapp.image.Image
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.render.Render


@Composable
fun ImageCarouselPaged(
    render: Render,
    viewState: ImageCarouselViewState,
    modifier: Modifier = Modifier,
) {
    val imageViewStates = viewState.imageViewStates
    ImageCarouselPaged(
        itemsCount = imageViewStates.size,
        modifier = modifier,
        autoSlideDuration = viewState.autoChangeDuration,
        userScrollEnabled = viewState.userScrollEnabled,
        initialPage = viewState.initialPage,
        onPageChanged = viewState.onPageChanged,
    ) { index ->
        Image(
            render = render,
            viewState = imageViewStates[index],
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarouselPaged(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = 3500,
    userScrollEnabled: Boolean = false,
    initialPage: Int = 0,
    onPageChanged: ((index: Int) -> Unit)? = null,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val pageCount = Int.MAX_VALUE
    val pagerState = rememberPagerState(
        initialPage = initialPage,
    ) { pageCount }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val index = page % itemsCount
//            println("ImageCarousel: index=$index")
            onPageChanged?.invoke(index)
        }
    }

    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoSlideDuration)
            val nextPage = pagerState.currentPage + 1
            pagerState.animateScrollToPage(
                nextPage,
//                animationSpec = spring(stiffness = (Spring.StiffnessLow + Spring.StiffnessVeryLow) / 2)
                animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            )
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = userScrollEnabled,
        ) { page ->
            val index = page % itemsCount
            itemContent(index)
        }
    }
}
