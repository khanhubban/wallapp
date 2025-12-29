package wallapp.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.content.model.Id
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.index.IndexTab
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.initialization.InitializationState
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.ScreenArgument
import wallapp.viewmodel.ViewModel

abstract class AppStateManager : ViewModel() {

    abstract val isUiReady: StateFlow<Boolean>
    suspend fun waitForUiReady() {
        isUiReady
            .filter { it }
            .first()
    }

    abstract val mediaInitializationState: StateFlow<InitializationState>
    suspend fun waitForMediaReady() {
        mediaInitializationState
            .filter { it == InitializationState.Ready }
            .first()
    }

    abstract val appViewState: Flow<AppViewState>
    abstract val navigationEvent: Flow<NavigationEvent>

    abstract fun navigateToScreen(id: Id)
    abstract fun navigateToScreen(arguments: ScreenArgument)

    abstract fun navigateToIndexTab(indexTab: IndexTab)

    abstract fun navigateBack()
    abstract fun dismissSearch()
    abstract fun dismissSearchInput()
    abstract fun dismissModalBottomSheet(onDismiss: () -> Unit = {})
    abstract fun dismissBottomSheet(onDismiss: (() -> Unit)? = null)

    abstract fun navigateToSearch()

    abstract fun navigateToOssLicenses()

    abstract fun navigateToPaywall(
        autoTriggerPurchase: Boolean = false,
        subscriptionExpired: Boolean = false,
        subscriptionPlan: SubscriptionPlan? = null,
    )
    abstract fun navigateToManageSubscription()

    abstract fun navigateToAccount()
    abstract fun navigateToArtists()

    abstract fun navigateToFirstRun()
    abstract fun navigateToSignUp()
    abstract fun navigateToDataConsent()

    abstract fun navigateToDebugSettings()

    abstract fun navigateToRewardAd(rewardAdCallbacks: RewardAdCallbacks)

    abstract fun navigateToError(errorScreen: ErrorScreen)
}