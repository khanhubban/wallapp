package wallapp.content.state.showcase.ads

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
@SealedInterop.Enabled
sealed class ShowcaseAdsViewState : ScreenViewState {

    @Immutable
    data object Loading : ShowcaseAdsViewState()

    @Immutable
    data class Data(
        val feedViewState: FeedViewState,
    ) : ShowcaseAdsViewState()
}
