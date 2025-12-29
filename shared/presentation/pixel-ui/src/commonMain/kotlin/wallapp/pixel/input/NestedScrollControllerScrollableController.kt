package wallapp.pixel.input

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.log.LogLevel
import wallapp.log.Logger
import wallapp.pixel.util.isZero
import wallapp.string.quote
import kotlin.math.abs

internal class NestedScrollControllerScrollableController(
    currentYOffsetPx: Animatable<Float, AnimationVector1D>,
    private val maxHeightPx: Float,
    private val coroutineScope: CoroutineScope,
    private val getHasEndpoint: () -> Boolean,
    private val setHasEndpoint: (Boolean) -> Unit,
    private val scrollScaler: Float = 1f,
    loggingTag: String = "",
) : NestedScrollController {

    companion object {
        const val TreatAsOpenFraction = .3f
    }

    // This code is pretty raw, so keep logging for now.
    @Suppress("PrivatePropertyName")
    private val Log = Logger(
        loggingEnabled = true,
        minLogLevel = LogLevel.Verbose,
        tag = "${loggingTag}NestedScrollConnection",
    )

    private val currentYOffsetPixels: Animatable<Float, AnimationVector1D> = currentYOffsetPx
    override val currentYOffsetPx: Float
        get() = currentYOffsetPixels.value

    private var isDragging = false
        set(value) {
            if (field != value) {
                Log.d("isDragging: $value")
                field = value
            }
        }

    override fun show() {
        animateVisibility(visible = true)
    }

    override fun hide() {
        animateVisibility(visible = false)
    }

    private var hasEndpoint: Boolean
        get() = getHasEndpoint()
        set(value) {
            Log.w("setHasEndpoint(hasEndpoint: $value)")
            setHasEndpoint(value)
        }

    private fun resetHasEndpoint() {
        if (hasEndpoint) {
            Log.w("resetHasEndpoint()")
            hasEndpoint = false
        }
    }

    private fun animateTo(targetValue: Float, via: String) {
        if (currentYOffsetPixels.value == targetValue) {
            Log.v("[via ${via.quote()}] animateTo() ignore because $targetValue currentYOffsetPx.value == targetValue")
            return
        }

        Log.w("[via ${via.quote()}] animateTo(targetValue: $targetValue)")
        coroutineScope.launch {
            currentYOffsetPixels.animateTo(
                targetValue = targetValue,
            )

            onAnimationFinished(via)
        }
    }

    private fun onAnimationFinished(via: String) {
        Log.w("[via ${via.quote()}] onAnimationFinished()")
        resetHasEndpoint()
        if (!isDragging) {
            animateVisibilityChecked()
        }
    }

    private fun animateVisibility(visible: Boolean) {
        Log.i("animateVisibility(visible: $visible)")
        setHasEndpoint(true)
        animateTo(
            targetValue = if (visible) 0f else -maxHeightPx,
            via = "animateVisibility()",
        )
    }

    // Animate to a visible or hidden state based on current offset
    private fun animateVisibility() {
        val openFraction = 1f - (currentYOffsetPixels.value * -1f / maxHeightPx)
        val visible = openFraction > TreatAsOpenFraction
        Log.i("animateVisibility(openFraction: $openFraction, visible: $visible)")
        animateVisibility(visible = visible)
    }

    private fun animateVisibilityChecked() {
        val openFraction = 1f - (currentYOffsetPixels.value * -1f / maxHeightPx)
        if (openFraction > 0f && openFraction < 1f) {
            Log.e("animateVisibilityChecked() animate! openFraction: $openFraction")
            animateVisibility()
        } else {
            Log.i("animateVisibilityChecked() ignore because openFraction: $openFraction")
        }
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource) {
        Log.d("onPreScroll available: $available, source: $source")

        if (source == NestedScrollSource.Drag) {
            isDragging = true
        }

        if (!hasEndpoint) {
            val delta = available.y * scrollScaler
            val newOffset = (currentYOffsetPixels.value + delta).coerceIn(-maxHeightPx, 0f)
            animateTo(newOffset, via = "onPreScroll()")
        } else {
            Log.v("onPreScroll() ignore because hasEndpoint==true")
        }
    }

    private val canAnimate: Boolean
        get() {
            val offsetPx = abs(currentYOffsetPixels.value)
            return (offsetPx > 0f && offsetPx < maxHeightPx)
        }

    private var lastPostScrollConsumed: Offset = Offset.Zero
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ) {
        Log.d("onPostScroll consumed: $consumed, available: $available, source: $source")

        /**
         * This check worked under Android, but triggers unexpectedly on iOS. Use
         * [onAnimationFinished] instead.
         */
//        if (source == NestedScrollSource.Fling && consumed.isZero && available.isZero) {
//            Log.e("onPostScroll() Fling Zero!")
//            resetHasEndpoint()
//            animateVisibilityChecked()
//        }

        val consumedY = consumed.y
        val lastConsumedY = lastPostScrollConsumed.y
        val directionChange = (lastConsumedY > 0f && consumedY < 0f)
                || (lastConsumedY < 0f && consumedY > 0f)
        if (source == NestedScrollSource.Drag && directionChange) {
            Log.e("onPostScroll() Drag change direction!")
            resetHasEndpoint()
        } else if (source == NestedScrollSource.Fling && directionChange) {
            Log.e("onPostScroll() Fling change direction!")
            resetHasEndpoint()
        }

        lastPostScrollConsumed = consumed

        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPreFling(available: Velocity) {
        isDragging = false
        Log.i("onPreFling available: $available")

        if (canAnimate && !available.isZero) {
            if (available.y > 0f) {
                Log.w("onPreFling() animate! visible = true")
                animateVisibility(visible = true)
            } else if (available.y < 0f) {
                Log.w("onPreFling() animate! visible = false")
                animateVisibility(visible = false)
            }
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity) {
        isDragging = false
        Log.i("onPostFling consumed: $consumed, available: $available")

//        if (canAnimate) {
            if (consumed.isZero && available.isZero) {
                Log.w("onPostFling Offset.Zero")
                onAnimationFinished("onPostFling() Offset.Zero")
            } else {
                Log.w("onPostScroll consumed: ${consumed.x} / ${consumed.y}, available: ${available.x} / ${available.y}")
            }
//        }
    }

    init {
        Log.w("init(): maxHeightPx: $maxHeightPx, currentYOffsetPx: ${currentYOffsetPx.value}")
    }
}

