package wallapp.content.state.ad

import wallapp.ads.inline.support.InlineAdItem
import wallapp.pixel.view.ViewState

// Note: cannot be Immutable because of the InlineAdItem
data class FeedAdViewState(
    val inlineAdItem: InlineAdItem,
    val fallbackAdViewState: AdViewState,
    val id: Int? = null // needed for iOS to uniquely identify the ad in the feed
) : ViewState
