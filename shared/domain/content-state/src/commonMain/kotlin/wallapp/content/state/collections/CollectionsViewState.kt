package wallapp.content.state.collections

import androidx.compose.runtime.Immutable
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
sealed interface CollectionsViewState : ScreenViewState {

    @Immutable
    data object Error : CollectionsViewState

    @Immutable
    data object Loading : CollectionsViewState

    @Immutable
    data class Ready(
        val feedViewState: FeedViewState,
    ): CollectionsViewState
}
