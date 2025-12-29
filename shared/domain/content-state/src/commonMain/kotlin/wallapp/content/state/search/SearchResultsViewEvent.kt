package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
sealed interface SearchResultsViewEvent : ViewEvent {

    @Immutable
    data object CloseSearch : SearchResultsViewEvent

}