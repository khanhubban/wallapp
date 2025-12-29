package wallapp.content.state.wallpaper

import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.ad.reward.RewardAdConfig
import wallapp.ad.reward.RewardAdWatchManager
import wallapp.app.AppStateManager
import wallapp.app.AppViewModelFactory
import wallapp.appconfig.AppConfig
import wallapp.apphistory.AppHistoryManager
import wallapp.bottomsheet.BottomSheetViewStateProvider
import wallapp.bottomsheet.BottomSheetViewStateProviderFactory
import wallapp.bottomsheet.BottomSheetViewStateProviderManager
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperRemix
import wallapp.content.model.WallpaperScreenTheme
import wallapp.content.model.remix
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.wallpaper.WallpaperShowcaseViewEvent.FollowIndicationAnimStarted
import wallapp.content.state.wallpaper.WallpaperShowcaseViewEvent.NavigateToArtist
import wallapp.content.state.wallpaper.WallpaperShowcaseViewEvent.ShareWallpaper
import wallapp.content.state.wallpaper.WallpaperShowcaseViewEvent.ToggleFollowArtist
import wallapp.coroutine.collectIn
import wallapp.data.artist.Artist
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.WallpaperContentResult
import wallapp.data.entitlement.EntitlementState
import wallapp.data.entitlement.isSubscriberAny
import wallapp.data.entitlement.isUnlockedHd
import wallapp.data.following.FollowingRepository
import wallapp.data.following.setFollowing
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.entitlement.EntitlementRepository
import wallapp.log.Logger
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEvent
import wallapp.pixel.view.ViewEventHandler
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.search.content.SearchContentRepository
import wallapp.system.platform.PlatformFeature
import wallapp.system.share.SystemShareManager
import wallapp.util.combine
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel
import wallapp.wallpaper.actionbutton.WallpaperShowcaseActionButtonManager
import wallapp.wallpaper.actionbutton.WallpaperShowcaseActionButtonState
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager

@OptIn(ExperimentalCoroutinesApi::class)
class WallpaperShowcaseViewModel(
    private val argument: WallpaperShowcaseScreenArgument,
    activeWallpaperDownloadManager: ActiveWallpaperDownloadManager,
    contentRepository: ContentRepository,
    viewStateRefresher: ViewStateRefresher,
    val entitlementRepository: EntitlementRepository,
    val appStateManager: AppStateManager,
    private val appHistoryManager: AppHistoryManager,
    private val contentColorManager: ContentColorManager,
    private val rewardAdConfig: RewardAdConfig,
    private val rewardAdWatchManager: RewardAdWatchManager,
    private val followingRepository: FollowingRepository,
    private val wallpaperShowcaseActionButtonManager: WallpaperShowcaseActionButtonManager,
    private val wallpaperDownloadManager: WallpaperDownloadManager,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val systemShareManager: SystemShareManager,
    private val viewStateFactory: WallpaperShowcaseViewStateFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val appConfig: AppConfig,
    private val strings: StringRepository,
    private val searchContentRepository: SearchContentRepository,
    private val bottomSheetViewStateProviderManager: BottomSheetViewStateProviderManager,
    private val appViewModelFactory: AppViewModelFactory,
) : ViewModel(),
    ScreenSystemBarControllerHolder,
    ScreenViewStateProvider,
    BottomSheetViewStateProviderFactory {

    companion object {
        val Log = Logger("[WallpaperShowcaseViewModel]")
    }

    private val initialRemixId: RemixId
        get() = argument.remixId
    private val displaysAsBottomSheet: Boolean
        get() = appConfig.spotlightBottomSheet.value

    private val spotlightTheme: WallpaperScreenTheme
        get() = appConfig.spotlightTheme.value

    private val overlayScreenViewState: StateFlow<ScreenViewState?> = MutableStateFlow(null)
    private var animateFollowIndicator = false
        set(value) {
//            Log.d("[LottieFixes] animateFollowIndicator: $value")
            field = value
        }

    private fun toggleFollowArtist() {
        val artistId = data.value[initialRemixId]?.artist?.id ?: return
        val followState = data.value[initialRemixId]?.artistFollowState ?: return
//        Log.d("[LottieFixes] toggleFollowArtist()")
        if (followState.isFollowing == false) {
            animateFollowIndicator = true
            followingRepository.setFollowing(artistId, followState = followState)
        }
        navigateToArtist()
    }

    private fun navigateToArtist() {
        val artistId = data.value[initialRemixId]?.artist?.id ?: return
        appStateManager.navigateToScreen(artistId)
    }

    private val artist: Artist?
        get() = data.value[initialRemixId]?.artist

    private val WallpaperRemix.shareUrl: String
        get() = "https://example.com/${slugs!!.first()}"

    private fun performShare(wallpaper: WallpaperRemix) {
        if (!systemShareManager.isAvailable) {
//            toastDisplayController.showToast("TODO: Add Sharing")
            return
        }

        val artist = artist
        val shareText = if (artist != null) {
            strings.shareWallpaperCopy(
                wallpaper.remix.collectionLabel,
                artist.name,
                wallpaper.shareUrl,
            )
        } else {
            strings.shareWallpaperCopy(
                wallpaper.remix.collectionLabel,
                wallpaper.shareUrl,
            )
        }
        systemShareManager.share(shareText)
    }

    private suspend fun onShareClick() {
        selectedWallpaper
            .filterNotNull()
            .first()
            .let { performShare(it) }
    }

    private val eventSink: (ViewEvent) -> Unit = { event ->
        when (event) {
            ToggleFollowArtist -> toggleFollowArtist()
            NavigateToArtist -> navigateToArtist()
            ShareWallpaper -> viewModelScope.launch { onShareClick() }
            FollowIndicationAnimStarted -> animateFollowIndicator = false
        }
    }

    private val toggleFollowEventHandler: ViewEventHandler = ViewEventHandler.Event(
        eventSink = eventSink,
        event = ToggleFollowArtist,
    )

    private val navigateToArtistEventHandler: ViewEventHandler = ViewEventHandler.Event(
        eventSink = eventSink,
        event = NavigateToArtist,
    )

    private val shareEventHandler: ViewEventHandler = ViewEventHandler.Event(
        eventSink = eventSink,
        event = ShareWallpaper,
    )

    private val followAnimStartedEventHandler: ViewEventHandler = ViewEventHandler.Event(
        eventSink = eventSink,
        event = FollowIndicationAnimStarted,
    )

    private fun createViewState(
        isUiReady: Boolean,
        data: Map<RemixId, WallpaperContentResult>,
        entitlementState: EntitlementState?,
        selectedIndex: Int,
        actionButton: MenuItem?,
        overlayScreenViewState: ScreenViewState?,
    ): WallpaperShowcaseViewState {
        if (!isUiReady || actionButton == null) {
            return WallpaperShowcaseViewState.Loading
        }
        val selectedRemixId = data[initialRemixId]?.collectionState?.wallpapers?.map { it.id }?.getOrNull(selectedIndex) ?: initialRemixId

        /**
         * Arbitrate this rather than use the value from [useLightTopControls]. This works around a
         * timing issue causing a visual glitch. #1485.
         */
        val useLightTopControls = useLightTopControls(data, data[selectedRemixId]?.wallpaper)
        val (controlButtonBackgroundColor, controlButtonOnBackgroundColor) = if (useLightTopControls) {
            contentColorManager.controlButtonBackgroundColorLight.value to
                    contentColorManager.controlButtonOnBackgroundColorLight.value
        } else {
            contentColorManager.controlButtonBackgroundColorDark.value to
                    contentColorManager.controlButtonOnBackgroundColorDark.value
        }

        return viewStateFactory.createViewState(
            data,
            entitlementState,
            selectedIndex,
            selectedRemixId,
            actionButton,
            onPageChangedWallpaperPreview,
            toggleFollowEventHandler = toggleFollowEventHandler,
            navigateToArtistEventHandler = navigateToArtistEventHandler,
            shareEventHandler = shareEventHandler,
            overlayScreen = overlayScreenViewState,
            animateFollowIndicator = animateFollowIndicator,
            followAnimStartedEventHandler = followAnimStartedEventHandler,
            controlButtonBackgroundColor,
            controlButtonOnBackgroundColor,
        )
    }

    private val onPageChangedWallpaperPreview: (Int) -> Unit = { index ->
//        Log.d("onPageChangedWallpaperPreview: $index")
        selectedIndex.value = index
    }

    private val data: StateFlow<Map<RemixId, WallpaperContentResult>> =
        contentRepository.getAllWallpaperContents(remixId = initialRemixId, firstWallpaperId = argument.firstWallpaperId)
            .stateIn(initialValue = emptyMap())

    private val selectedIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    private val selectedWallpaper: StateFlow<WallpaperRemix?> = combine(
        selectedIndex,
        data,
    ) { selectedIndex, data ->
        val collectionWallpapers = data[initialRemixId]?.collectionState?.wallpapers
        (if (selectedIndex == -1 || collectionWallpapers == null) {
            data[initialRemixId]?.wallpaper
        } else {
            collectionWallpapers.getOrNull(selectedIndex) ?: collectionWallpapers.firstOrNull()
        }).also {
            Log.d("selectedItem: ${it?.id}, selectedIndex: $selectedIndex, collectionWallpapers.size: ${collectionWallpapers?.size}")
        }
    }.stateIn(initialValue = null)

    private val defaultUserLightTopControls: Boolean
        get() = true
    private fun useLightTopControls(dataMap: Map<RemixId, WallpaperContentResult>, selectedWallpaper: WallpaperRemix?): Boolean {
        if (selectedWallpaper == null) return defaultUserLightTopControls
        val data = dataMap[selectedWallpaper.id] ?: return defaultUserLightTopControls
        return data.useLightTopControls
    }
    private val useLightTopControls: Flow<Boolean> =
        combine(data, selectedWallpaper) { dataMap, selectedWallpaper ->
            useLightTopControls(dataMap, selectedWallpaper)
        }

    private val selectedWallpaperDownloadState: Flow<WallpaperDownloadState?> = selectedWallpaper
        .filterNotNull()
        .flatMapLatest { wallpaperDownloadManager.getWallpaperDownloadState(it.id) }
        .stateIn(initialValue = null)

    private val selectedCollectionWallpaperDownloadButton: Flow<MenuItem?> by lazy {
        combine(
            data,
            selectedWallpaper,
            heroButtonMode,
            selectedEntitlementState,
        ) { data, selectedWallpaper, heroButtonMode, entitlementState ->
            if (heroButtonMode is WallpaperHeroButtonMode.GetCollection) {
                val collection = data[initialRemixId]?.collectionState
                if (collection != null && selectedWallpaper != null && entitlementState != null) {
                    viewStateFactory.createCollectionWallpaperDownloadButton(
                        collection,
                        selectedWallpaper,
                        heroButtonMode.label,
                        entitlementState
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private val navigateToBottomSheet: (WallpaperId) -> Unit = { wallpaperId ->
        val viewState = viewState.value
        val height = bottomSheetHeight.value?.value
        if (height != null && viewState is WallpaperShowcaseViewState.Success) {
            appStateManager.navigateToScreen(
                ScreenArgument.WallpaperSingleActionScreenArgument(
                    wallpaperId = wallpaperId,
                    modalSheetHeight = height,
                )
            )
        }
    }

    private val selectedWallpaperShowcaseActionButtonStateUnlocked: Flow<WallpaperShowcaseActionButtonState> =
        selectedWallpaper
            .filterNotNull()
            .flatMapLatest {
                wallpaperShowcaseActionButtonManager
                    .getWallpaperShowcaseActionButtonState(
                        wallpaperId = it.id,
                        staticWallpaperSizeStandardResolution = StaticWallpaperSize.StandardResolution,
                        staticWallpaperSizeFullResolution = StaticWallpaperSize.FullResolution,
                        navigateToBottomSheet = navigateToBottomSheet,
                    )
            }
            .stateIn(
                initialValue = WallpaperShowcaseActionButtonState.None(
                    initialRemixId,
                    viewStateFactory.createPlaceholderActionButton()
                )
            )

    private var updatedAdditionalIndex = false
    private fun updateSelectedIndex(data: Map<RemixId, WallpaperContentResult>) {
        val collectionWallpapers = data[initialRemixId]?.collectionState?.wallpapers
        val indexOf = collectionWallpapers?.indexOfFirst { it.id == initialRemixId } ?: -1

        if (!updatedAdditionalIndex && collectionWallpapers != null && indexOf != -1) {
            Log.i("One time update: selectedIndex: $indexOf / (${collectionWallpapers.size})")
            selectedIndex.value = indexOf
            updatedAdditionalIndex = true
        }
    }

    /**
     * Use this instead of [WallpaperContentResult.entitlementState], so that the value
     * is up to date as the selected wallpaper changes (which happens in a Collection).
     */
    private val selectedEntitlementState: StateFlow<EntitlementState?> = selectedWallpaper
        .flatMapLatest { wallpaper ->
            if (wallpaper != null) {
                entitlementRepository.getEntitlementState(wallpaper.id)
            } else {
                flowOf(null)
            }
        }.stateIn(initialValue = null)

    private val wallpaperIsUnlockedHd: StateFlow<Boolean> = selectedEntitlementState
        .map { it?.isUnlockedHd == true }
        .stateIn(initialValue = false)

    private val heroButtonMode: Flow<WallpaperHeroButtonMode> = combine(
        rewardAdConfig.enabled,
        data,
        wallpaperIsUnlockedHd,
        rewardAdWatchManager.getRemainingAdWatchCount(argument.remixId),
        rewardAdConfig.maxAdsToUnlockASingle,
    ) { _, data, _, _, _ ->
        val collection = data.values.firstOrNull { it.collectionState != null }?.collectionState

        if (collection != null) {
            WallpaperHeroButtonMode.GetCollection(
                label = strings.getCollection,
            )
        } else {
            WallpaperHeroButtonMode.GetWallpaper(
                label = strings.getWallpaper,
            )
        }
    }

    // The height is determined by the number of buttons on the sheet. This is a bit messy, but
    // required so the iOS sheet opens at the correct height.
    private val bottomSheetHeight: StateFlow<Dp?> = combine(
        heroButtonMode,
        selectedEntitlementState,
    ) { heroButtonMode, entitlementState ->
        if (heroButtonMode is WallpaperHeroButtonMode.GetCollection) {
            viewSpecArbitrator.bottomSheetHeightTwoOptions
        } else {
            if (entitlementState?.isSubscriberAny == false) {
                viewSpecArbitrator.bottomSheetHeightThreeOptions
            } else
                viewSpecArbitrator.bottomSheetHeightTwoOptions
            }
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    private val actionButton: Flow<MenuItem?> = combine(
        appStateManager.isUiReady,
        data,
        heroButtonMode,
        selectedEntitlementState,
        selectedWallpaper,
        wallpaperIsUnlockedHd,
        selectedWallpaperShowcaseActionButtonStateUnlocked,
        selectedCollectionWallpaperDownloadButton,
    ) {
      isUiReady,
      _,
      heroButtonMode,
      _,
      _,
      _,
      selectedWallpaperShowcaseActionButtonStateUnlocked,
      selectedCollectionWallpaperDownloadButton,
        ->
        if (!isUiReady) {
            return@combine null
        }

        when (heroButtonMode) {
            is WallpaperHeroButtonMode.GetCollection -> {
                selectedCollectionWallpaperDownloadButton
            }
            is WallpaperHeroButtonMode.GetWallpaper -> {
                selectedWallpaperShowcaseActionButtonStateUnlocked.buttonViewState
            }
        }
    }.stateIn(initialValue = viewStateFactory.createPlaceholderActionButton())

    @FlowInterop.Enabled
    override val viewState: StateFlow<WallpaperShowcaseViewState> = combine(
        appStateManager.isUiReady,
        data,
        selectedEntitlementState,
        selectedIndex,
        actionButton,
        heroButtonMode,
        overlayScreenViewState,
        viewStateRefresher.refresh,
    ) { isUiReady, data, entitlementState, currentIndex, actionButton, heroButtonMode, overlayScreenViewState, _, ->
//        Log.d("overlayScreenViewState: $overlayScreenViewState")
        updateSelectedIndex(data)
        createViewState(
            isUiReady,
            data,
            entitlementState,
            currentIndex,
            actionButton,
            overlayScreenViewState,
        )
    }.stateIn(initialValue = WallpaperShowcaseViewState.Loading)

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        combine(overlayScreenViewState, useLightTopControls) {
            overlayScreenViewState, useLightTopControls ->
            if (overlayScreenViewState != null) {
                ScreenSystemBarController.TranslucentStatusBar()
            } else if (spotlightTheme == WallpaperScreenTheme.WallpaperPalette) {
                ScreenSystemBarController.TranslucentStatusBar()
            } else {
                ScreenSystemBarController.TranslucentStatusBar(
                    darkStatusBarIcons = if (PlatformFeature.SystemBottomSheetsSupported) {
                        null
                    } else {
                        false//!useLightTopControls
                    },
                )
            }
        }.stateIn(initialValue = ScreenSystemBarController.Default)
    }

    override fun createBottomSheetScreenViewStateProvider(screenArgument: ScreenArgument?):
            BottomSheetViewStateProvider {
        requireNotNull(screenArgument)
        return when (screenArgument) {
            is ScreenArgument.WallpaperSingleActionScreenArgument -> {
                appViewModelFactory.createWallpaperSingleActionViewModel(screenArgument)
            }
            is ScreenArgument.CollectionActionScreenArgument -> {
                appViewModelFactory.createCollectionActionViewModel(screenArgument)
            }
            else -> {
                throw IllegalArgumentException("Unsupported argument: $screenArgument")
            }
        }.let {
            it as BottomSheetViewStateProvider
        }
    }

    override fun onCleared() {
        Log.d("onCleared(): $argument")

        bottomSheetViewStateProviderManager.unregister(this)
        super.onCleared()
    }

    init {
        Log.d("init(): $argument")

        bottomSheetViewStateProviderManager.register(this)

        appHistoryManager.registerWallpaperView(initialRemixId)

        selectedWallpaperDownloadState.collectIn(viewModelScope) {
            Log.i("[WallpaperDownloadState] : $it")
        }

        selectedWallpaper.collectIn(viewModelScope) { wallpaper ->
            wallpaperSystemPhotoStatusManager.setPollSystemPhotoStatusForId(wallpaper?.id)

            if (wallpaper != null) {
                searchContentRepository.getSearchRemixMetadata(wallpaper.id)
                    .collectIn(viewModelScope) {
                        if (it != null) {
                            Log.d(it.debugString)
                        }
                    }
            }
        }
    }
}