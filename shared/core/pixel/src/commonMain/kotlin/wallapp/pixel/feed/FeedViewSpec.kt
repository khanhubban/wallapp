package wallapp.pixel.feed

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewSpec


@Immutable
data class FeedViewSpec(
    val columns: Int,
    val staggeredGrid: Boolean,
    // If true, the last item in a row will stretch to fill the remaining space.
    // If false, such dangling items will have blank space to their right.
    val stretchDanglingNonMaxSpanItems: Boolean = true,
    val itemWidth: Dp,
    val itemSpacingHorizontal: Dp = 16.dp,
    val itemSpacingVertical: Dp = 16.dp,
    val paddingHorizontal: Dp = 16.dp,
    val paddingVertical: Dp = 0.dp,
    val offsetYAnimated: Dp = 0.dp,
    val shapeSpec: ShapeSpec? = null,
): ViewSpec {

    companion object {
        val Preset: FeedViewSpec = FeedViewSpec(
            columns = 2,
            staggeredGrid = false,
            itemWidth = 120.dp,
        )
    }
}