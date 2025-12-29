package wallapp.pixel.input

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlin.math.max

class NestedScrollExploreToolbarController(
    currentYOffsetPx: MutableState<Float>,
    private val maxHeightPx: Float,
    private val topStickyOffset: Float,
    private val feedScrollController: NestedScrollConsumingFeedController,
) : NestedScrollController {

    private val currentYOffsetPixels = if (currentYOffsetPx.value == 0f) {
        currentYOffsetPx.value = topStickyOffset
        currentYOffsetPx
    } else {
        currentYOffsetPx
    }
    override val currentYOffsetPx: Float
        get() = currentYOffsetPixels.value

    override fun onPreScroll(available: Offset, source: NestedScrollSource) {
        val feedOffset = feedScrollController.currentYOffsetPx
//        Log.d("[NestedScrollExploreToolbarController] onPreScroll() currentYOffsetPx: $currentYOffsetPx, feedOffset: $feedOffset, availableY: ${available.y}")
        if (available.y < 0f) { // we only care about updating the offset in onPreScroll when scrolling up
            if (feedOffset > 0f) {
                currentYOffsetPixels.value = feedOffset
            } else {
                val newOffset = currentYOffsetPixels.value + available.y
                val clampedOffset = newOffset.coerceIn(-maxHeightPx, topStickyOffset)
                currentYOffsetPixels.value = clampedOffset
            }
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource) {
        val feedOffset = feedScrollController.currentYOffsetPx
//        Log.d("[NestedScrollExploreToolbarController] onPostScroll() currentYOffsetPx: $currentYOffsetPx, feedOffset: $feedOffset, consumedY: ${consumed.y}")
        // No similar check for consumed here as consumed becomes 0 when the feed is scrolled to the top
        if (feedOffset > 0f) {
            currentYOffsetPixels.value = max(feedOffset, currentYOffsetPixels.value)
        } else if (consumed.y > 0f) {
            val newOffset = currentYOffsetPixels.value + consumed.y
            val clampedOffset = newOffset.coerceIn(-maxHeightPx, topStickyOffset)
            currentYOffsetPixels.value = clampedOffset
        }
    }

    override fun hide() {
        // no-op
    }

    override fun show() {
        // no-op
    }
}