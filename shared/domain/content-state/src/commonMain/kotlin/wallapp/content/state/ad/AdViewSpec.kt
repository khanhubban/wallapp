package wallapp.content.state.ad

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.unit.Padding

@Immutable
data class AdViewSpec(
    val height: Dp = 386.dp,
    val imageHeight: Dp = 200.dp,
    val closeButtonPadding: Padding?,
    override val useMaxItemSpan: Boolean = true,
) : FeedViewViewSpec