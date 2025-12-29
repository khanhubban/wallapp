package wallapp.pixel.input

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import wallapp.log.Log

class NestedScrollConsumingFeedController(
    currentYOffsetPx: MutableState<Float>,
    val maxOffset: Float,
) : NestedScrollConsumingController {

    private var _currentYOffsetPx = currentYOffsetPx
    override val currentYOffsetPx: Float
        get() = _currentYOffsetPx.value

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        Log.d("[NestedScrollConsumingFeedController] onPreScroll() available: $available, source: $source")
        return if (available.y < 0) {
            consumeAndUpdateOffset(available, "onPreScroll")
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        Log.d("[NestedScrollConsumingFeedController] onPostScroll() consumed: $consumed, available: $available")
        return if (available.y > 0f) {
            consumeAndUpdateOffset(available, "onPostScroll")
        } else {
            Offset.Zero
        }
    }

    private fun consumeAndUpdateOffset(available: Offset, source: String): Offset {
        val newYOffsetPx = _currentYOffsetPx.value + available.y
        val clampedYOffsetPx = newYOffsetPx.coerceIn(0f, maxOffset)
        val consumedY = clampedYOffsetPx - _currentYOffsetPx.value
        _currentYOffsetPx.value = clampedYOffsetPx

        Log.d("[NestedScrollConsumingFeedController] [$source] consumedY: $consumedY, _currentYOffsetPx: $_currentYOffsetPx")
        return Offset(x = 0f, y = consumedY)
    }
}