package wallapp.content.state.error.rewardad

import androidx.compose.runtime.Immutable
import wallapp.content.state.upgrade.plus.promo.UpgradePromoViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.theme.Theme


@Immutable
sealed interface ErrorRewardAdViewState : ScreenViewState {

    @Immutable
    data object Loading : ErrorRewardAdViewState

    @Immutable
    data class Success(
        val viewSpec: ErrorRewardAdViewSpec,
        val theme: Theme,
        val toolbarViewState: ToolbarViewState,
        val messages: List<MenuItem>,
        val actionButton: MenuItem,
        val errorMessage: MenuItem?,
        val upgradePromoViewState: UpgradePromoViewState,
    ) : ErrorRewardAdViewState

}