package wallapp.pixel.paging

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.view.ViewEvent

@Immutable
@SealedInterop.Enabled
sealed class PagingViewEvent : ViewEvent {

    @Immutable
    data object LoadMore : PagingViewEvent()
}

typealias PagingViewEventSink = (PagingViewEvent) -> Unit