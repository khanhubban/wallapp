package wallapp.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.log.Log
import wallapp.pixel.navigation.NavigationEvent
import wallapp.pixel.navigation.NavigationManager
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.system.platform.PlatformFeature

class AppNavigatorDefault(
    private val config: AppNavigatorConfig,
    private val navigationManager: NavigationManager,
    private val rewardAdNavigator: RewardAdNavigator,
    private val currentScreenProvider: CurrentScreenProvider,
    private val coroutineScopeMain: CoroutineScope,
) : AppNavigator {

    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)

    override val currentScreenFlow: StateFlow<Screen?>
        get() = currentScreenProvider.currentScreenFlow
    override val currentScreen: Screen?
        get() = currentScreenProvider.currentScreen()
    private val currentScreenIsRoot: Boolean
        get() = currentScreen in config.appRootScreens

    override val finishCurrentActivity: MutableSharedFlow<Unit> = MutableSharedFlow()

    override val navigationEvent: Flow<NavigationEvent>
        get() = navigationManager.navigationEvent

    private fun sendNavigationEvent(event: NavigationEvent) {
        Log.d("[Navigation] sendNavigationEvent(event = $event)")
        coroutineScopeMain.launch {
            navigationManager.sendNavigationEvent(event)
        }
    }

    private var screenChangesAreBlocked = false

    override fun toScreen(screenArgument: ScreenArgument, clearScreenStack: Boolean) {
        if (screenChangesAreBlocked) {
            Log.w("[Navigation] screenChangesAreBlocked == true, ignoring toScreen($screenArgument)")
            return
        }

        sendNavigationEvent(
            NavigationEvent.ToScreenEvent(screenArgument, clearScreenStack)
        )
    }

    override fun toScreenBlockingUseWithExtremeCaution(screenArgument: ScreenArgument) {
        screenChangesAreBlocked = true
        sendNavigationEvent(
            NavigationEvent.ToScreenEvent(screenArgument, clearScreenStack = true)
        )
    }

    override fun popScreen() {
        if (PlatformFeature.CheckForRootScreenOnPop && currentScreenIsRoot) {
            val currentScreen = currentScreen
            coroutineScopeMain.launch {
                Log.w("[Navigation] popScreen() currentScreen: $currentScreen, finishing Activity")
                finishCurrentActivity.emit(Unit)
            }
        } else {
            sendNavigationEvent(NavigationEvent.PopScreenEvent)
        }
    }

    override fun showRewardAd(rewardAdCallbacks: RewardAdCallbacks) {
        rewardAdNavigator.showRewardAd(rewardAdCallbacks)
    }
}