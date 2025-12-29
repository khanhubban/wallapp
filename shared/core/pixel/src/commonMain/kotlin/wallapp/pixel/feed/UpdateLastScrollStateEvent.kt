package wallapp.pixel.feed

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
data class UpdateLastScrollStateEvent(
    val lastScrollState: FeedScrollState?,
) : ViewEvent

typealias LastScrollStateUpdateSink = (UpdateLastScrollStateEvent) -> Unit
