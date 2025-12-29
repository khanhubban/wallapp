package wallapp.content.state.index

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.navigationbar.NavigationBarViewState
import wallapp.pixel.pager.PagerState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.util.DpOptional
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Immutable
@SealedInterop.Enabled
sealed class IndexViewState : ScreenViewState {

    @Immutable
    data object Loading : IndexViewState()

    @Immutable
    data class Success(
        val screens: List<ScreenViewState>,
        val overlayScreen: ScreenViewState?,
        val overlayVisible: Boolean,
        val onOverlayVisibilityProgress: (Float) -> Unit,
        val onOverlayDismissed: () -> Unit,
        val currentScreenIndex: Int,
        val currentScreenViewState: ScreenViewState,
        val navigationBar: NavigationBarViewState,
        val scrollableTopBarHeight: DpOptional?,
        val usePager: Boolean,
        val onPagerStateChange: (PagerState) -> Unit,
        val screenCrossfadeDuration: Duration = 400.milliseconds,
    ): IndexViewState()
}
