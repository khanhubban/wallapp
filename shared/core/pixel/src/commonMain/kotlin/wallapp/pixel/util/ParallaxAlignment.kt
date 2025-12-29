package wallapp.pixel.util

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Stable
class ParallaxAlignment(
    val horizontalMaxScale: Float = 1f,
    val verticalMaxScale: Float = 1f,
    private val horizontalBias: () -> Float = { 0f },
    private val verticalBias: () -> Float = { 0f },
) : Alignment {

    override fun align(
        size: IntSize,
        space: IntSize,
        layoutDirection: LayoutDirection,
    ): IntOffset {
        // Convert to Px first and only round at the end, to avoid rounding twice while calculating
        // the new positions
        val centerX = (space.width - size.width).toFloat() / 2f
        val centerY = (space.height - size.height).toFloat() / 2f
        val resolvedHorizontalBias = if (layoutDirection == LayoutDirection.Ltr) {
            horizontalBias()
        } else {
            -1 * horizontalBias()
        }

        val x = centerX * (1 + resolvedHorizontalBias)
        val y = centerY * (1 + verticalBias())
        return IntOffset(x.roundToInt(), y.roundToInt())
    }
}

fun ParallaxAlignment(
    lazyStaggeredGridState: LazyStaggeredGridState,
    key: Any,
    verticalMaxScale: Float = 1f,
    horizontalMaxScale: Float = 1f,
): ParallaxAlignment {
    val verticalBias = verticalMaxScale > 1f
    val horizontalBias = horizontalMaxScale > 1f
    require(verticalMaxScale >= 1f) {"verticalBias must be >= 1f" }
    require(horizontalMaxScale >= 1f) {"horizontalBias must be >= 1f" }
    require(!horizontalBias) {"TODO: Implement horizontalBias"}

    return ParallaxAlignment(
        verticalMaxScale = verticalMaxScale,
        verticalBias = {
            if (verticalBias) {
                val layoutInfo = lazyStaggeredGridState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                val itemInfo = visibleItemsInfo.firstOrNull {
                    it.key == key
                }

                if (itemInfo != null) {
                    val itemCenterY = itemInfo.offset.y + (itemInfo.size.height / 2)
                    val viewportCenterY = layoutInfo.viewportStartOffset + (layoutInfo.viewportSize.height / 2)
                    val adjustedOffset = itemCenterY - viewportCenterY
                    val scaledOffset = adjustedOffset.toFloat() / (layoutInfo.viewportSize.height / 2)
                    scaledOffset.coerceIn(-1f, 1f)
//                        .also {
//                            Log.d("parallax: $it, adjustedOffset: $adjustedOffset, itemCenterY: $itemCenterY, itemInfo.size.height: ${itemInfo.size.height}")
//                        }
                } else {
                    0f
                }
            } else {
                0f
            }
        },
    )
}


fun ParallaxAlignment(
    lazyGridState: LazyGridState,
    key: Any,
    verticalMaxScale: Float = 1f,
    horizontalMaxScale: Float = 1f,
): ParallaxAlignment {
    val verticalBias = verticalMaxScale > 1f
    val horizontalBias = horizontalMaxScale > 1f
    require(verticalMaxScale >= 1f) {"verticalBias must be >= 1f" }
    require(horizontalMaxScale >= 1f) {"horizontalBias must be >= 1f" }
    require(!horizontalBias) {"TODO: Implement horizontalBias"}

    return ParallaxAlignment(
        verticalMaxScale = verticalMaxScale,
        verticalBias = {
            if (verticalBias) {
                val layoutInfo = lazyGridState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                val itemInfo = visibleItemsInfo.firstOrNull {
                    it.key == key
                }

                if (itemInfo != null) {
                    val itemCenterY = itemInfo.offset.y + (itemInfo.size.height / 2)
                    val viewportCenterY = layoutInfo.viewportStartOffset + (layoutInfo.viewportSize.height / 2)
                    val adjustedOffset = itemCenterY - viewportCenterY
                    val scaledOffset = adjustedOffset.toFloat() / (layoutInfo.viewportSize.height / 2)
                    scaledOffset.coerceIn(-1f, 1f)
//                        .also {
//                            Log.d("parallax: $it, adjustedOffset: $adjustedOffset, itemCenterY: $itemCenterY, itemInfo.size.height: ${itemInfo.size.height}")
//                        }
                } else {
                    0f
                }
            } else {
                0f
            }
        },
    )
}

fun ParallaxAlignmentVertical(
    lazyStaggeredGridState: LazyStaggeredGridState,
    key: Any,
    maxParallaxScale: Float,
): ParallaxAlignment {
    return ParallaxAlignment(
        lazyStaggeredGridState = lazyStaggeredGridState,
        key = key,
        verticalMaxScale = maxParallaxScale,
        horizontalMaxScale = 1f,
    )
}

fun ParallaxAlignmentVertical(
    lazyGridState: LazyGridState,
    key: Any,
    maxParallaxScale: Float,
): ParallaxAlignment {
    return ParallaxAlignment(
        lazyGridState = lazyGridState,
        key = key,
        verticalMaxScale = maxParallaxScale,
        horizontalMaxScale = 1f,
    )
}