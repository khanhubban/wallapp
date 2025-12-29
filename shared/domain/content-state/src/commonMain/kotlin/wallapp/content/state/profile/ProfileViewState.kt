package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
@SealedInterop.Enabled
sealed class ProfileViewState : ScreenViewState {

    @Immutable
    data object Loading : ProfileViewState()

    @Immutable
    data class Data(
        val feedViewState: FeedViewState,
    ) : ProfileViewState()
}

