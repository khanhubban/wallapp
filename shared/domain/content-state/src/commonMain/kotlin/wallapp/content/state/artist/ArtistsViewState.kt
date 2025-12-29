package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
sealed interface ArtistsViewState : ScreenViewState {

    @Immutable
    data object Loading : ArtistsViewState

    @Immutable
    data class Success(
        val feedViewState: FeedViewState,
    ): ArtistsViewState
}
