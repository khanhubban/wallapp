package wallapp.content.state.folder

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.swipetodismiss.OnSwipeToDismiss

@Immutable
@SealedInterop.Enabled
sealed class FolderViewState : ScreenViewState {

    @Immutable
    data object Loading : FolderViewState()

    @Immutable
    data class Success(
        val viewSpec: FolderViewSpec,
        val toolbarViewState: FolderToolbarViewState,
        val feedViewState: FeedViewState,
        val onSwipeToDismiss: OnSwipeToDismiss?,
    ): FolderViewState()
}
