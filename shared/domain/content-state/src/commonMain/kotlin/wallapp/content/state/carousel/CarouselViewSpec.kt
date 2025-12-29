package wallapp.content.state.carousel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.unit.Padding
import wallapp.view.ViewSpecDefaults

@Immutable
data class CarouselViewSpec(
    val height: Dp,
    val extraHeightForScroll: Dp,
    val pageWidth: Dp,
    val pageSpacing: Dp,
    val edgeButtonWidth: Dp,
    val centerPage: Boolean,
    val transformEdgeItems: Boolean,
    val applyInfiniteScroll: Boolean,
    val indicatorContainerHeight: Dp,
    val indicatorSpacing: Dp = 4.dp,
    val indicatorSize: Dp = ViewSpecDefaults.PaddingMedium / 2,
    val padding: Padding? = null,
) : FeedViewViewSpec {
    override val useMaxItemSpan: Boolean
        get() = true
    override val useZeroFeedPadding: Boolean
        get() = true
}
