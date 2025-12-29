package wallapp.pixel.feed

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewSpec

interface FeedViewViewSpec : ViewSpec {

    val useMaxItemSpan: Boolean?
        get() = false

    val useZeroFeedPadding: Boolean?
        get() = false
}

@Immutable
data class FeedViewViewSpecDefault(
    override val useMaxItemSpan: Boolean? = false,
    override val useZeroFeedPadding: Boolean? = false,
) : FeedViewViewSpec


val MaxFeedWidthViewSpec = FeedViewViewSpecDefault(true)
val MaxFeedWidthNoPaddingViewSpec = FeedViewViewSpecDefault(true, useZeroFeedPadding = true)