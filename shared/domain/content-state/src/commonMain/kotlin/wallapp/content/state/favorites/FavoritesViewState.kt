package wallapp.content.state.favorites

import androidx.compose.runtime.Immutable
import wallapp.content.state.widget.NoDataViewState
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
sealed interface FavoritesViewState : ScreenViewState {

    @Immutable
    data object Loading : FavoritesViewState

    @Immutable
    data class Success(
        val feedViewState: FeedViewState,
    ): FavoritesViewState

    @Immutable
    data class NoData(
        val viewState: NoDataViewState,
    ): FavoritesViewState
}
