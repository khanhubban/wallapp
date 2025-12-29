package wallapp.content.state.rewardadinternal

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.image.Image
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.view.ViewEventHandler
import wallapp.theme.Theme

@Immutable
@SealedInterop.Enabled
sealed class RewardAdInternalViewState : ScreenViewState {

    @Immutable
    data object Loading : RewardAdInternalViewState()

    @Immutable
    data class Data(
        val theme: Theme,
        // Typically a video
        val heroMedia: ImageViewState?,
        // Typically a static image
        val fallbackMedia: ImageViewState,
        val closeButton: MenuItem,
        val callToAction: MenuItem,
        val disclaimer: MenuItem,
        val statusBarShadow: Image,
        val footerShadow: Image,
        val rewardStatus: MenuItem,
        val rewardStatusViewEventHandler: ViewEventHandler,
        val onClickContent: ViewEventHandler,
        val onBack: ViewEventHandler,
    ) : RewardAdInternalViewState() {

        init {
            require(heroMedia != null || fallbackMedia != null) {
                "At least one of heroMedia or fallbackMedia must be non-null"
            }
        }
    }
}