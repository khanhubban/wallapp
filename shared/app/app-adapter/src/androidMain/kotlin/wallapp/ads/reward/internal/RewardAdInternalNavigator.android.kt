package wallapp.ads.reward.internal

import android.app.Activity
import wallapp.activity.SingleScreenActivity
import wallapp.screen.ScreenArgument
import wallapp.system.ui.controller.UiControllerManagerAndroid

class RewardAdInternalNavigatorAndroid(
    private val uiControllerManager: UiControllerManagerAndroid,
) : RewardAdInternalNavigator {

    private val currentActivity: Activity?
        get() = uiControllerManager.currentUiController?.activity

    override fun show(): Boolean {
        val activity = currentActivity ?: return false

        SingleScreenActivity.start(activity, ScreenArgument.RewardAdInternalScreenArgument)
        return true
    }

    override fun hide(): Boolean {
        val activity = currentActivity ?: return false
        if (activity is SingleScreenActivity) {
            activity.finish()
            return true
        }

        return false
    }
}