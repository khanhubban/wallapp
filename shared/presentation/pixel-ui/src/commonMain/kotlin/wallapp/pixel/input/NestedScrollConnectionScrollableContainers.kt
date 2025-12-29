package wallapp.pixel.input

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope

/**
 * Handles a list of [controllers]. Note that all of the overrides return default values, so
 * no input is consumed.
 */
class NestedScrollConnectionScrollableContainers(
    private val controllers: List<NestedScrollController>,
    private val consumingController: NestedScrollConsumingController?,
) : NestedScrollConnection {

    fun hide() {
        controllers.forEach {
            it.hide()
        }
    }

    fun show() {
        controllers.forEach {
            it.show()
        }
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val consumedOffset = consumingController?.onPreScroll(available = available, source = source) ?: Offset.Zero
        controllers.forEach {
            it.onPreScroll(available = available, source = source)
        }
        return consumedOffset
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val consumedOffset = consumingController?.onPostScroll(consumed = consumed, available = available, source = source) ?: Offset.Zero
        controllers.forEach {
            it.onPostScroll(consumed = consumed, available = available, source = source)
        }
        return consumedOffset
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        controllers.forEach {
            it.onPreFling(available = available)
        }
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        controllers.forEach {
            it.onPostFling(consumed = consumed, available = available)
        }
        return super.onPostFling(consumed, available)
    }
}

@Composable
fun rememberNestedScrollConnectionScrollableContainers(
    controllers: List<NestedScrollController>,
    consumingController: NestedScrollConsumingController?,
): NestedScrollConnection {
    return remember(controllers) {
        NestedScrollConnectionScrollableContainers(controllers = controllers, consumingController = consumingController)
    }
}

@Composable
fun rememberNestedScrollController(
    currentYOffsetPx: Animatable<Float, AnimationVector1D>,
    maxHeightPx: Float,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    loggingTag: String = "",
): NestedScrollController {
    var hasEndpoint by remember { mutableStateOf(false) }
    val scrollScaler = 1.82f

    return remember(currentYOffsetPx, maxHeightPx, coroutineScope) {
        NestedScrollControllerScrollableController(
            currentYOffsetPx = currentYOffsetPx,
            maxHeightPx = maxHeightPx,
            coroutineScope = coroutineScope,
            getHasEndpoint = { hasEndpoint },
            setHasEndpoint = { value -> hasEndpoint = value },
            scrollScaler = scrollScaler,
            loggingTag = loggingTag,
        )
    }
}

@Composable
fun rememberNestedScrollExploreToolbarController(
    currentYOffsetPx: MutableState<Float>,
    maxHeightPx: Float,
    topStickyOffset: Float,
    consumingController: NestedScrollConsumingFeedController,
): NestedScrollController {
    return remember(maxHeightPx) {
        NestedScrollExploreToolbarController(
            currentYOffsetPx = currentYOffsetPx,
            maxHeightPx = maxHeightPx,
            topStickyOffset = topStickyOffset,
            feedScrollController = consumingController,
        )
    }
}
