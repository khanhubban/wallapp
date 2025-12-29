package wallapp.content.state.explore

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.view.ViewEvent

@Immutable
@SealedInterop.Enabled
sealed class ExploreViewState : ScreenViewState {

    @Immutable
    data object Loading : ExploreViewState()

    @Immutable
    data class Success(
        val feedViewState: FeedViewState,
        val highlightCarouselViewState: HighlightCarouselViewState?,
        val lastFeedOffsetUpdateSink: LastFeedOffsetUpdateSink,
        val feedOffset: Float?,
        // Note: this is called from Compose rendering, not iOS. See #2028.
        val statusBarBackgroundAlphaOnChange: (Float) -> Unit,
    ) : ExploreViewState()

}

@Immutable
data class UpdateLastFeedOffsetEvent(
    val lastFeedOffset: Float?,
) : ViewEvent

typealias LastFeedOffsetUpdateSink = (UpdateLastFeedOffsetEvent) -> Unit