package wallapp.pixel.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState as FoundationPagerState

@OptIn(ExperimentalFoundationApi::class)
val FoundationPagerState.pagerState: PagerState
    get() = PagerState(
        currentPage = currentPage,
        targetPage = targetPage,
        settledPage = settledPage,
        isScrollInProgress = isScrollInProgress,
        currentPageOffsetFraction = currentPageOffsetFraction,
    )

@OptIn(ExperimentalFoundationApi::class)
val FoundationPagerState.toDebugString: String
    get() = "currentPage: $currentPage, settledPage: $settledPage, targetPage: $targetPage, " +
            "isScrollInProgress: $isScrollInProgress, currentPageOffsetFraction: $currentPageOffsetFraction, " +
            "interactionSource: $interactionSource"
