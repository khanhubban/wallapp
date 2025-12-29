package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
@SealedInterop.Enabled
sealed class SearchResultsViewState : ScreenViewState {

    @Immutable
    data object Inactive : SearchResultsViewState()

    @Immutable
    data object Loading : SearchResultsViewState()

    @Immutable
    data class Data(
        val feedViewState: FeedViewState,
        val headerViewState: SearchResultsHeaderViewState,
        val hasResults: Boolean, // currently not used but added when SearchResultsViewState.NoResults was removed
    ) : SearchResultsViewState()
}

fun SearchResultsViewState?.stateOrNull(): SearchResultsViewState? {
    if (this is SearchResultsViewState.Inactive) return null
    return this
}