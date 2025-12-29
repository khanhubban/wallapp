package wallapp.pixel.pager

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.TransformOrigin
import wallapp.math.lerp
import kotlin.math.abs


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerScope.TransformPagerEdgeItems(
    page: Int,
    pagerState: PagerState,
    itemsSize: Int,
    minScale: Float = 0.82f,
    maxScale: Float = 1f,
    minAlpha: Float = 0.4f,
    maxAlpha: Float = 1f,
    content: @Composable PagerScope.(scale: Float, alpha: Float, transformOrigin: TransformOrigin) -> Unit,
) {
    // Correctly calculate the offset for infinite scrolling
    val realPage = page % itemsSize
    val currentPage = pagerState.currentPage % itemsSize
    val pageDiff = realPage - currentPage
    val pageOffset = if (pageDiff < -itemsSize / 2) {
        itemsSize + pageDiff + pagerState.currentPageOffsetFraction
    } else if (pageDiff > itemsSize / 2) {
        pageDiff - itemsSize + pagerState.currentPageOffsetFraction
    } else {
        pageDiff + pagerState.currentPageOffsetFraction
    }

    val isCurrentPage = page == pagerState.currentPage

    val normalizedPageOffset = if (!isCurrentPage) {
        if (pageOffset <= -1f) { // left item
            1f - abs(pageOffset + 1)
        } else if (pageOffset >= 1f) { // right item
            1f - (pageOffset - 1f)
        } else {
            pageOffset
        }
    } else {
        pageOffset
    }

    val scale = lerp(minScale, maxScale, maxScale - abs(normalizedPageOffset).coerceIn(0f, 1f))

    val alpha = lerp(minAlpha, maxAlpha, maxAlpha - abs(normalizedPageOffset).coerceIn(0f, 1f))

    // Correctly calculate the transform so the next/previous items are aligned correctly
    val targetTransformFractionX = when {
        page < pagerState.currentPage -> 1f // Page is on the left
        page > pagerState.currentPage -> 0f // Page is on the right
        else -> 0.5f // Current page
    }
    // Animate the fraction to avoid a snap as the item becomes the current page
    val animatedTransformFractionX by animateFloatAsState(
        targetValue = targetTransformFractionX,
        animationSpec = tween(durationMillis = 150)
    )

    val transformOrigin = TransformOrigin(animatedTransformFractionX, 0.5f)

    content(scale, alpha, transformOrigin)
}
