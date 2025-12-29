package wallapp.content.state.error.rewardad

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import wallapp.app.AppStateManager
import wallapp.content.model.Id.RemixId
import wallapp.content.state.error.ErrorScreen
import wallapp.coroutine.collectIn
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.UnlockWallpaperContentResult
import wallapp.data.entitlement.EntitlementState
import wallapp.network.NetworkState
import wallapp.network.isConnected
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventSink
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.system.navigation.SystemNavigator
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel


class ErrorRewardAdViewModel(
    private val argument: ScreenArgument.ErrorScreenArgument,
    contentRepository: ContentRepository,
    private val viewStateFactory: ViewStateFactory,
    private val appStateManager: AppStateManager,
    private val networkState: NetworkState,
    private val privacyMessagingManager: PrivacyMessagingManager,
    private val systemNavigator: SystemNavigator,
    private val strings: StringRepository,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    companion object {
        private var forceErrorRewardAdMode: ErrorRewardAdMode? = null//ErrorRewardAdMode.AdConsentDenied
    }

    private val errorScreen: ErrorScreen.RewardAd
        get() = argument.errorScreen as ErrorScreen.RewardAd
    private val errorMessage: String?
        get() = errorScreen.errorMessage?.let { strings.error(it) }
    private val wallpaperId: RemixId
        get() = errorScreen.wallpaperId

    private val eventSink: (ErrorRewardAdViewEvent) -> Unit = { event ->
        when (event) {
            is ErrorRewardAdViewEvent.OpenNetworkSettings -> {
                systemNavigator.toSystemNetworkSettings()
            }

            ErrorRewardAdViewEvent.OpenPrivacySettings -> {
                privacyMessagingManager.showPrivacyOptions()
            }
        }
    }

    private val content: StateFlow<UnlockWallpaperContentResult?> =
        contentRepository.getUnlockWallpaperContent(wallpaperId).stateIn(null)

    private val entitlementState: Flow<EntitlementState?> = content.map { it?.entitlementState }

    fun arbitrateErrorRewardAdMode(
        networkIsConnected: Boolean = networkState.isConnected,
        canRequestAds: Boolean = privacyMessagingManager.canRequestAds,
    ): ErrorRewardAdMode {
        forceErrorRewardAdMode?.let { return it }

        return when {
            !networkIsConnected -> ErrorRewardAdMode.NetworkError
            !canRequestAds -> ErrorRewardAdMode.AdConsentDenied
            else -> ErrorRewardAdMode.Other
        }
    }

    private val mode: StateFlow<ErrorRewardAdMode> = networkState.networkConnectionState
        .map { arbitrateErrorRewardAdMode() }
        .stateIn(initialValue = arbitrateErrorRewardAdMode())

    private fun createViewState(mode: ErrorRewardAdMode = this.mode.value): ErrorRewardAdViewState {
        return viewStateFactory.createErrorRewardAdViewState(
            errorRewardAdMode = mode,
            eventSink = eventSink as ViewEventSink,
            errorMessage = errorMessage,
        )
    }

    override val viewState: StateFlow<ErrorRewardAdViewState> = combine(
        mode,
        content,
        entitlementState,
        viewStateRefresher.refresh,
    ) { mode, content, _, _, ->
        createViewState()
    }
        .stateIn(initialValue = createViewState())

    init {
        combine(mode, networkState.networkConnectionState) {
            mode, networkConnectionState ->
            mode to networkConnectionState
        }.collectIn(viewModelScope) { (mode, networkConnectionState) ->
            if (mode == ErrorRewardAdMode.NetworkError
                && networkConnectionState.isConnected
            ) {
                appStateManager.navigateBack()
            }
        }
    }

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> =
        MutableStateFlow(ScreenSystemBarController.TranslucentStatusBar(darkStatusBarIcons = true))
}
