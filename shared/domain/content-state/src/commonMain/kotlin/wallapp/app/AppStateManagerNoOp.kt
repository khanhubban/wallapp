package wallapp.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.content.model.Id
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.index.IndexTab
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.initialization.InitializationState
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.ScreenArgument

class AppStateManagerNoOp : AppStateManager() {

    override val isUiReady: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    override val mediaInitializationState: StateFlow<InitializationState>
        get() = TODO("Not yet implemented")

    override val appViewState: Flow<AppViewState>
        get() = TODO("Not yet implemented")

    override val navigationEvent: Flow<NavigationEvent>
        get() = TODO("Not yet implemented")

    override fun navigateToScreen(id: Id) = Unit

    override fun navigateToScreen(arguments: ScreenArgument) = Unit

    override fun navigateToIndexTab(indexTab: IndexTab) = Unit

    override fun navigateBack() = Unit

    override fun dismissSearch() = Unit

    override fun dismissSearchInput() = Unit

    override fun dismissModalBottomSheet(onDismiss: () -> Unit) = Unit

    override fun dismissBottomSheet(onDismiss: (() -> Unit)?) = Unit

    override fun navigateToSearch() = Unit

    override fun navigateToOssLicenses() = Unit

    override fun navigateToPaywall(autoTriggerPurchase: Boolean, subscriptionExpired: Boolean, subscriptionPlan: SubscriptionPlan?) = Unit

    override fun navigateToManageSubscription() = Unit

    override fun navigateToAccount() = Unit

    override fun navigateToArtists() = Unit

    override fun navigateToFirstRun() = Unit

    override fun navigateToSignUp() = Unit

    override fun navigateToDataConsent() = Unit

    override fun navigateToDebugSettings() = Unit

    override fun navigateToRewardAd(rewardAdCallbacks: RewardAdCallbacks) = Unit

    override fun navigateToError(errorScreen: ErrorScreen) = Unit
}