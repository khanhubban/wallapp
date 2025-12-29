package wallapp.content.state.toolbar

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
data class CollapsingToolbarPersistableState(
    val height: Int,
    val offsetY: Int,
    val scrollStrategy: String,
)

@Immutable
data class UpdateLastCollapsingToolbarStateEvent(
    val state: CollapsingToolbarPersistableState,
) : ViewEvent

typealias LastCollapsingToolbarStateUpdateSink = (UpdateLastCollapsingToolbarStateEvent) -> Unit

@Immutable
data class CollapsingToolbarStateWrapper(
    val lastCollapsingToolbarState: CollapsingToolbarPersistableState?,
    val lastCollapsingToolbarStateUpdateSink: LastCollapsingToolbarStateUpdateSink?,
)