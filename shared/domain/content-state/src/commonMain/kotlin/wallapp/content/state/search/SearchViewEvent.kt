package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.view.ViewEvent
import wallapp.search.model.SearchColor

@Immutable
@SealedInterop.Enabled
sealed class SearchViewEvent : ViewEvent {

    @Immutable
    data object Clear : SearchViewEvent()

    @Immutable
    data object CloseSearch : SearchViewEvent()

    @Immutable
    data class QueryChange(val query: String?) : SearchViewEvent()

    @Immutable
    data class QuerySubmit(val query: String?) : SearchViewEvent()

    @Immutable
    data class QueryFocused(val focused: Boolean) : SearchViewEvent()

    @Immutable
    data class SearchColorToggle(val color: SearchColor) : SearchViewEvent()
}

typealias SearchViewEventSink = (SearchViewEvent) -> Unit
