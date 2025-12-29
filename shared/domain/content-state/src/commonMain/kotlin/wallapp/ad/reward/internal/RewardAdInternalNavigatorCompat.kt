package wallapp.ad.reward.internal

import wallapp.ads.reward.internal.RewardAdInternalNavigator
import wallapp.app.AppStateManager
import wallapp.screen.ScreenArgument

class RewardAdInternalNavigatorCompat(
    private val appStateManager: AppStateManager,
) : RewardAdInternalNavigator {

    override fun show(): Boolean {
        appStateManager.navigateToScreen(ScreenArgument.RewardAdInternalScreenArgument)
        return true
    }

    override fun hide(): Boolean {
        appStateManager.navigateBack()
        return false
    }
}