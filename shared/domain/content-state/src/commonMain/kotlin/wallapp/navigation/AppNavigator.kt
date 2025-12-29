package wallapp.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument

interface AppNavigator {

    val isReady: StateFlow<Boolean>
    val currentScreenFlow: StateFlow<Screen?>
    val currentScreen: Screen?

    /**
     * Emits when a navigation event occurs. This should be used very sparingly.
     */
    val navigationEvent: Flow<NavigationEvent>

    /**
     * Android-specific flag. Activities should subscribe to this and finish themselves when it
     * emits.
     */
    val finishCurrentActivity: Flow<Unit>

    fun toScreen(screenArgument: ScreenArgument, clearScreenStack: Boolean = false)
    fun popScreen()
    fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks)

    /**
     * Navigates to [screenArgument] and then prevents any further calls to [toScreen]
     * from having an effect. Cannot be turned off.
     *
     * This is intended for use in extreme cases, such as when the user is using an old
     * version of the app that must be updated, and the current version of the app will not allow
     * any other screens to display until it is updated.
     */
    fun toScreenBlockingUseWithExtremeCaution(screenArgument: ScreenArgument)
}