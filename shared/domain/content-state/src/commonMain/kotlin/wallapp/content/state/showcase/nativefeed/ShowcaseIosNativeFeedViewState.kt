package wallapp.content.state.showcase.nativefeed

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.screen.ScreenViewState

@Immutable
@SealedInterop.Enabled
sealed class ShowcaseIosNativeFeedViewState : ScreenViewState {

    @Immutable
    data object Loading : ShowcaseIosNativeFeedViewState()

    @Immutable
    data class Data(
        val feedViewState: FeedViewState,
    ) : ShowcaseIosNativeFeedViewState()
}
