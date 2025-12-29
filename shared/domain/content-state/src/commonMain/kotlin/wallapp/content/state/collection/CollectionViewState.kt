package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.swipetodismiss.OnSwipeToDismiss

@Immutable
@SealedInterop.Enabled
sealed class CollectionViewState : ScreenViewState {

    @Immutable
    data object Loading : CollectionViewState()

    @Immutable
    data class Success(
        val toolbarViewState: CollectionToolbarViewState,
        val feedViewState: FeedViewState,
        val onSwipeToDismiss: OnSwipeToDismiss?,
    ): CollectionViewState()
}
