package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
@SealedInterop.Enabled
sealed class HomeOnboardingViewState : ScreenViewState {

    @Immutable
    data object Loading : HomeOnboardingViewState()

    @Immutable
    data class Success(
        val feedViewState: FeedViewState,
    ): HomeOnboardingViewState()
}
