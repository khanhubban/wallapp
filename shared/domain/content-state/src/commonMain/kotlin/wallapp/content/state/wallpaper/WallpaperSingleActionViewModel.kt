package wallapp.content.state.wallpaper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import wallapp.ad.reward.RewardAdConfig
import wallapp.ad.reward.RewardAdWatchManager
import wallapp.ads.AdError
import wallapp.ads.AdErrorCode
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.ads.reward.RewardAdManager
import wallapp.app.AppStateManager
import wallapp.bottomsheet.BottomSheetViewStateProvider
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.error.ErrorScreen
import wallapp.coroutine.collectIn
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.UnlockWallpaperContentResult
import wallapp.data.entitlement.EntitlementState
import wallapp.data.entitlement.isSubscriberAny
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.entitlement.EntitlementRepository
import wallapp.log.Logger
import wallapp.network.NetworkConnectionState
import wallapp.network.NetworkState
import wallapp.pixel.menu.MenuItem
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument.WallpaperSingleActionScreenArgument
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.view.menu.MenuItemFactory
import wallapp.viewmodel.ViewModel
import wallapp.wallpaper.actionbutton.WallpaperActionButtonManager
import wallapp.wallpaper.actionbutton.WallpaperActionButtonState
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager

class WallpaperSingleActionViewModel(
    private val argument: WallpaperSingleActionScreenArgument,
    private val rewardAdConfig: RewardAdConfig,
    contentRepository: ContentRepository,
    private val entitlementRepository: EntitlementRepository,
    private val appStateManager: AppStateManager,
    private val rewardAdManager: RewardAdManager,
    private val rewardAdWatchManager: RewardAdWatchManager,
    wallpaperActionButtonManager: WallpaperActionButtonManager,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val networkState: NetworkState,
    private val menuItemFactory: MenuItemFactory,
    private val viewStateFactory: ViewStateFactory,
    private val viewEventFactory: ViewEventFactory,
    private val strings: StringRepository,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), BottomSheetViewStateProvider {

    companion object {
        val Log = Logger("WallpaperSingleActionViewModel")
    }

    private val wallpaperId: WallpaperId
        get() = argument.wallpaperId
    private var isRewardEarned = false

    private var wallpaper:WallpaperRemix? = null

    private val canShowRewardAdLoadingButton: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val onRewardEarned: () -> Unit = {
        canShowRewardAdLoadingButton.value = false
        isRewardEarned =
            rewardAdWatchManager.decrementRemainingAdWatchCountAndCheckIfUnlocked(wallpaperId)
        if (isRewardEarned) {
            entitlementRepository.setRewardEntitlement(wallpaperId)
            // Download Full-Res content upon reward being granted.
            wallpaper?.also {
                wallpaperActionButtonManager.downloadWallpaper(it, StaticWallpaperSize.FullResolution)
            }
        }
    }

    private val onRewardClosed: () -> Unit = {
        // Commented out for now because this is unreliable.
//        if (enableConsecutivePlays && !isRewardEarned) {
//            viewModelScope.launch {
//                delay(2000)
//                showRewardAd()
//            }
//        }
    }

    private val unlockWallpaperOnFailure: Boolean
        get() = rewardAdConfig.unlockWallpaperOnFailure.value

    private val onRewardError: (AdError) -> Unit = { adError ->
        when {
            // If the user has watched at least one ad for this wallpaper, grant the reward. #1143
            rewardAdWatchManager.hasPlayedAdForWallpaper(wallpaperId) -> {
                onRewardEarned()
            }
            // In the unlikely event that "unlockWallpaperOnFailure" remote config flag is enabled,
            // grant the reward. This is just a placeholder and can be removed once native reward
            // ads are available. #2005
            unlockWallpaperOnFailure
                    && networkState.isConnected
                    && adError.code != AdErrorCode.NetworkError -> {
                var isUnlocked = rewardAdWatchManager
                    .decrementRemainingAdWatchCountAndCheckIfUnlocked(wallpaperId)
                while (!isUnlocked) {
                    isUnlocked = rewardAdWatchManager
                        .decrementRemainingAdWatchCountAndCheckIfUnlocked(wallpaperId)
                }
                onRewardEarned()
            }
            else -> {
                appStateManager.navigateToError(
                    ErrorScreen.RewardAd(
                        wallpaperId = wallpaperId,
                        errorMessage = "${adError.code}: ${adError.message}",
                    )
                )
            }
        }
    }

    private val rewardAdCallbacks = RewardAdCallbacks(
        onRewardEarned = onRewardEarned,
        onRewardClosed = onRewardClosed,
        onRewardError = onRewardError,
    )

    private val content: StateFlow<UnlockWallpaperContentResult?> =
        contentRepository.getUnlockWallpaperContent(wallpaperId).stateIn(null)

    private val entitlementState: Flow<EntitlementState?> = content.map { it?.entitlementState }

    private val onHeroAction: () -> Unit = {
        appStateManager.navigateBack()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedWallpaperActionButtonStateSd: Flow<WallpaperActionButtonState> =
        canShowRewardAdLoadingButton.flatMapLatest {
            wallpaperActionButtonManager
                .getWallpaperActionWithDownloadButtonState(
                    wallpaperId,
                    canShowPlusButton = false,
                    canShowRewardAdLoadingButton = it,
                    onHeroAction = onHeroAction,
                    staticWallpaperSize = StaticWallpaperSize.StandardResolution,
                    showRewardAd = ::showRewardAd,
                )
                .stateIn(
                    initialValue = WallpaperActionButtonState.None(
                        wallpaperId,
                        viewStateFactory.createPlaceholderActionButton()
                    )
                )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedWallpaperActionButtonStateHd: Flow<WallpaperActionButtonState> =
        canShowRewardAdLoadingButton.flatMapLatest {
            wallpaperActionButtonManager
                .getWallpaperActionWithDownloadButtonState(
                    wallpaperId,
                    canShowPlusButton = false,
                    canShowRewardAdLoadingButton = it,
                    onHeroAction = onHeroAction,
                    staticWallpaperSize = StaticWallpaperSize.FullResolution,
                    showRewardAd = ::showRewardAd,
                )
                .stateIn(
                    initialValue = WallpaperActionButtonState.None(
                        wallpaperId,
                        viewStateFactory.createPlaceholderActionButton()
                    )
                )
        }

    private fun createViewState(
        wallpaper: Wallpaper? = null,
        entitlementState: EntitlementState,
        wallpaperActionButtonStateSd: WallpaperActionButtonState,
        wallpaperActionButtonStateHd: WallpaperActionButtonState,
    ): WallpaperSingleActionViewState {
        this.wallpaper = wallpaper
        val actionButtons = createActionButtons(
            entitlementState,
            wallpaperActionButtonStateSd,
            wallpaperActionButtonStateHd,
        )

        return viewStateFactory.createWallpaperSingleActionViewState(
            wallpaper = wallpaper,
            actionButton1 = actionButtons.first,
            actionButton2 = actionButtons.second,
            actionButton3 = actionButtons.third,
        )
    }

    override val viewState: StateFlow<WallpaperSingleActionViewState> = combine(
        content,
        entitlementState,
        entitlementRepository.getEntitlementState(wallpaperId),
        viewStateRefresher.refresh,
        rewardAdWatchManager.getRemainingAdWatchCount(wallpaperId),
        selectedWallpaperActionButtonStateSd,
        selectedWallpaperActionButtonStateHd,
    ) { content, _, entitlementState, _, remainingAdWatchCount, wallpaperActionButtonStateSd, wallpaperActionButtonStateHd ->
        createViewState(
            wallpaper = content?.wallpaper,
            entitlementState,
            wallpaperActionButtonStateSd = wallpaperActionButtonStateSd,
            wallpaperActionButtonStateHd = wallpaperActionButtonStateHd,
        )
    }.stateIn(initialValue = WallpaperSingleActionViewState.Loading)

    private fun showRewardAd() {
        if (networkState.isConnected) {
            viewEventFactory.createNavigateToRewardAd(callbacks = rewardAdCallbacks).invoke()
            canShowRewardAdLoadingButton.value = true
        }
    }

    private fun createActionButtons(
        entitlementState: EntitlementState,
        wallpaperActionButtonStateSd: WallpaperActionButtonState,
        wallpaperActionButtonStateHd: WallpaperActionButtonState,
    ): Triple<MenuItem, MenuItem, MenuItem?> {
        return Triple(
            wallpaperActionButtonStateSd.buttonViewState,
            wallpaperActionButtonStateHd.buttonViewState,
            if (!entitlementState.isSubscriberAny) {
                menuItemFactory.createPlusAdFreeButton()
            } else {
                null
            },
        )
    }

    override fun destroy() {
        onCleared()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("onCleared")
    }

    init {
        networkState.networkConnectionState.collectIn(viewModelScope) {
            if (it == NetworkConnectionState.Disconnected || it == NetworkConnectionState.ConnectionNoInternet) {
                appStateManager.navigateToError(ErrorScreen.Network())
            }
        }

        if (networkState.networkConnectionState.value == NetworkConnectionState.Connected) {
            rewardAdManager.loadRewardAd()
        }

        Log.d("init: $argument")
        wallpaperSystemPhotoStatusManager.setPollSystemPhotoStatusForId(wallpaperId)
    }
}