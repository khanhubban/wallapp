package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.swipetodismiss.OnSwipeToDismiss
import wallapp.pixel.tab.TabsViewState

@Immutable
@SealedInterop.Enabled
sealed class ArtistViewState : ScreenViewState {

    @Immutable
    data object Loading : ArtistViewState()

    @Immutable
    data class Success(
        val viewSpec: ArtistViewSpec,
        val artistToolbar: ArtistToolbarViewState,
        val tabs: TabsViewState,
        val onSwipeToDismiss: OnSwipeToDismiss?,
        val lastPagerStateUpdateSink: LastPagerStateUpdateSink?,
    ): ArtistViewState()
}
