package wallapp.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.content.model.Id
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.index.IndexTab
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.initialization.InitializationState
import wallapp.log.Log
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.ScreenArgument

/**
 * Wrapper class to allow AppStateManager to be injected across the app. Requires that
 * [setAppViewModel] be manually called with the actual [AppStateManager].
 */
class AppStateManagerWrapper(
    coroutineScopeMain: CoroutineScope,
) : AppStateManager() {

    private val appViewModelFlow = MutableStateFlow<AppViewModel?>(null)
    /**
     * Hack to work around DI creation order
     */
    private val appViewModel: AppViewModel
        get() = appViewModelFlow.value ?: error("AppViewModel not set")
    fun setAppViewModel(appViewModel: AppViewModel) {
        appViewModelFlow.value = appViewModel
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isUiReady: StateFlow<Boolean> =
        appViewModelFlow.filter { it != null }
            .filterNotNull()
            .flatMapLatest { it.isUiReady }
            .map { it }
            .onEach { Log.d("[AppStateManagerWrapper] isUiReady: $it") }
            .stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = false)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val mediaInitializationState: StateFlow<InitializationState> =
        appViewModelFlow.filter { it != null }
            .filterNotNull()
            .flatMapLatest { it.mediaInitializationState }
            .map { it }
            .onEach { Log.d("[AppStateManagerWrapper] isUiReady: $it") }
            .stateIn(
                coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = InitializationState.Uninitialized,
            )

    override val appViewState: Flow<AppViewState>
        get() = appViewModel.appViewState

    override val navigationEvent: Flow<NavigationEvent>
        get() = appViewModel.navigationEvent

    override fun navigateToScreen(id: Id) {
        appViewModel.navigateToScreen(id)
    }

    override fun navigateToScreen(arguments: ScreenArgument) {
        appViewModel.navigateToScreen(arguments)
    }

    override fun navigateToIndexTab(indexTab: IndexTab) {
        appViewModel.navigateToIndexTab(indexTab)
    }

    override fun navigateToSearch() {
        appViewModel.navigateToSearch()
    }

    override fun navigateBack() {
        appViewModel.navigateBack()
    }

    override fun dismissSearch() {
        appViewModel.dismissSearch()
    }

    override fun dismissSearchInput() {
        appViewModel.dismissSearchInput()
    }

    override fun dismissModalBottomSheet(onDismiss: () -> Unit) {
        appViewModel.dismissModalBottomSheet(onDismiss)
    }

    override fun dismissBottomSheet(onDismiss: (() -> Unit)?) {
        appViewModel.dismissBottomSheet(onDismiss)
    }

    override fun navigateToPaywall(
        autoTriggerPurchase: Boolean,
        subscriptionExpired: Boolean,
        subscriptionPlan: SubscriptionPlan?,
    ) {
        appViewModel.navigateToPaywall(autoTriggerPurchase, subscriptionExpired, subscriptionPlan)
    }

    override fun navigateToManageSubscription() {
        appViewModel.navigateToManageSubscription()
    }

    override fun navigateToOssLicenses() {
        appViewModel.navigateToOssLicenses()
    }

    override fun navigateToAccount() {
        appViewModel.navigateToAccount()
    }

    override fun navigateToArtists() {
        appViewModel.navigateToArtists()
    }

    override fun navigateToFirstRun() {
        appViewModel.navigateToFirstRun()
    }

    override fun navigateToSignUp() {
        appViewModel.navigateToSignUp()
    }

    override fun navigateToDataConsent() {
        appViewModel.navigateToDataConsent()
    }

    override fun navigateToDebugSettings() {
        appViewModel.navigateToDebugSettings()
    }

    override fun navigateToRewardAd(rewardAdCallbacks: RewardAdCallbacks) {
        appViewModel.navigateToRewardAd(rewardAdCallbacks)
    }

    override fun navigateToError(errorScreen: ErrorScreen) {
        appViewModel.navigateToError(errorScreen)
    }

    override fun onCleared() {
        appViewModel.onCleared()
    }
}