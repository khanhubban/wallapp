package wallapp.content.state.feed

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.shape.ShapeSpec

@Immutable
data class FeedContentPreviewViewSpec(
    val width: Dp,
    val height: Dp,
    val shapeSpec: ShapeSpec,
    override val useMaxItemSpan: Boolean? = false,
) : FeedViewViewSpec {

    companion object {
        val Preset = FeedContentPreviewViewSpec(
            shapeSpec = ShapeSpec.Preset,
            width = 140.dp,
            height = 264.dp,
        )
    }
}