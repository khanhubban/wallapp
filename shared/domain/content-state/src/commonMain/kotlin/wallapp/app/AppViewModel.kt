package wallapp.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.account.state.UserProfileErrorHandler
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.appconfig.AppConfig
import wallapp.appstate.AppState
import wallapp.billing.BillingSubscriptionExpiredHandler
import wallapp.bottomsheet.BottomSheetManagerCreateListener
import wallapp.bottomsheet.BottomSheetViewStateProvider
import wallapp.bottomsheet.BottomSheetViewStateProviderManager
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Id.StoryCollectionId
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.explore.ExploreRepository
import wallapp.content.state.home.HomeFilterManager
import wallapp.content.state.home.HomeViewStateFactory
import wallapp.content.state.index.IndexTab
import wallapp.content.state.index.IndexTabSpecFactory
import wallapp.content.state.index.IndexViewModel
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.coroutine.collectIn
import wallapp.data.content.ContentCacheManager
import wallapp.data.content.ContentRepository
import wallapp.entitlement.EntitlementManager
import wallapp.graphics.Color
import wallapp.graphics.lerp
import wallapp.inappupdate.InAppUpdateChecker
import wallapp.initialization.InitializationState
import wallapp.log.Log
import wallapp.mediamap.MediaMapRepository
import wallapp.navigation.AppNavigator
import wallapp.navigation.AppNavigatorDefault
import wallapp.onboarding.OnboardingManager
import wallapp.onboarding.OnboardingState
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.bottomsheet.BottomSheetManager
import wallapp.pixel.bottomsheet.BottomSheetState
import wallapp.pixel.bottomsheet.BottomSheetStateChange
import wallapp.pixel.bottomsheet.BottomSheetViewState
import wallapp.pixel.bottomsheet.ModalBottomSheetViewState
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.navigation.NavigationEvent
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.profileimage.ProfileImageManager
import wallapp.purchase.PurchaseUiManager
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remotepaywall.RemotePaywallManager
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.AccountScreenArgument
import wallapp.screen.ScreenArgument.ArtistIdScreenArgument
import wallapp.screen.ScreenArgument.ArtistsScreenArgument
import wallapp.screen.ScreenArgument.CollectionActionScreenArgument
import wallapp.screen.ScreenArgument.CollectionIdScreenArgument
import wallapp.screen.ScreenArgument.DataConsentScreenArgument
import wallapp.screen.ScreenArgument.ErrorScreenArgument
import wallapp.screen.ScreenArgument.FolderScreenArgument
import wallapp.screen.ScreenArgument.IndexScreenArgument
import wallapp.screen.ScreenArgument.OssLicensesScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.SearchScreenArgument
import wallapp.screen.ScreenArgument.SignUpScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.screen.ScreenArgument.WallpaperSingleActionScreenArgument
import wallapp.system.navigation.SystemNavigator
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.platform.PlatformFeature
import wallapp.theme.ThemeManager
import wallapp.time.TimeRepository
import wallapp.util.combine
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.view.shape.ShapeSpecFactory
import wallapp.viewmodel.ViewModel
import wallapp.viewmodel.ViewModelFactory


class AppViewModel(
    private val appNavigator: AppNavigator,
    private val contentRepository: ContentRepository,
    private val mediaMapRepository: MediaMapRepository,
    private val viewStateFactory: ViewStateFactory,
    private val viewStateFactoryHome: HomeViewStateFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val indexTabSpecFactory: IndexTabSpecFactory,
    private val contentCacheManager: ContentCacheManager,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val contentColorManager: ContentColorManager,
    private val exploreRepository: ExploreRepository,
    private val homeFilterManager: HomeFilterManager,
    appStateManager: AppStateManager,
    viewStateRefresher: ViewStateRefresher,
    private val viewModelFactory: ViewModelFactory,
    private val accountManager: AccountManager,
    private val profileImageManager: ProfileImageManager,
    private val messageBarManager: MessageBarManager,
    private val themeManager: ThemeManager,
    private val systemNavigator: SystemNavigator,
    private val alertManager: AlertManager,
    private val globalOverlayManager: GlobalOverlayManager,
    private val purchaseUiManager: PurchaseUiManager,
    private val appConfig: AppConfig,
    private val appState: AppState,
    private val onboardingManager: OnboardingManager,
    entitlementManager: EntitlementManager,
    private val remotePaywallManager: RemotePaywallManager,
    private val remoteConfigData: RemoteConfigData,
    private val systemPhotoPicker: SystemPhotoPicker,
    private val timeRepository: TimeRepository,
    private val bottomSheetViewStateProviderManager: BottomSheetViewStateProviderManager,
    private val billingSubscriptionExpiredHandler: BillingSubscriptionExpiredHandler,
    private val inAppUpdateChecker: InAppUpdateChecker,
    private val accountDataRepository: AccountDataRepository,
    private val appLaunchManager: AppLaunchManager,
    userProfileErrorHandler: UserProfileErrorHandler,
    coroutineScopeMain: CoroutineScope,
) : AppStateManager(), BottomSheetManagerCreateListener {

    private var bottomSheetManager: BottomSheetManager? = null
    private val bottomSheetScreenViewState: MutableStateFlow<ScreenViewState?> = MutableStateFlow(null)
    private val bottomSheetScrimColor: MutableStateFlow<Color?> = MutableStateFlow(null)
    private val bottomSheetViewState: StateFlow<BottomSheetViewState?> =
        combine(bottomSheetScreenViewState, bottomSheetScrimColor) {
           screenViewState, scrimColor ->
                createBottomSheetViewState(screenViewState, scrimColor)
        }.stateIn(null)

    private var bottomSheetViewStateJob: Job? = null
    private val isBottomSheetVisible: Boolean
        get() = bottomSheetManager?.state?.value?.let {
            return !(it == BottomSheetState.Hidden || it == BottomSheetState.Collapsed)
        } ?: false

    val isModalBottomSheetVisible: Boolean
        get() = modalBottomSheetViewState.value != null
    private val modalBottomSheetViewState: MutableStateFlow<ModalBottomSheetViewState?> =
        MutableStateFlow(null)

    private val indexSearchInputOverlayIsVisible: Boolean
        get() = indexViewModel.searchInputOverlayIsVisible.value &&
                (appNavigator.currentScreen == Screen.Explore || appNavigator.currentScreen == Screen.Index)
    private val indexSearchResultsIsVisible: Boolean
        get() = indexViewModel.showSearchResults.value &&
                (appNavigator.currentScreen == Screen.Explore || appNavigator.currentScreen == Screen.Index)

    val enableIosNativeNavigation: StateFlow<Boolean> = appConfig.enableIosNativeNavigation

    override fun onBottomSheetManagerCreate(bottomSheetManager: Any) {
//        Log.d("[BottomSheet] configure()")
        this.bottomSheetManager = bottomSheetManager as BottomSheetManager

        bottomSheetManager.state.collectIn(viewModelScope) { onBottomSheetState(it) }
        bottomSheetManager.stateChange.collectIn(viewModelScope) { onBottomSheetStateChange(it) }
    }

    private fun onBottomSheetState(state: BottomSheetState) {
//        Log.i("onBottomSheetState(): state: $state")
        if (state == BottomSheetState.Hidden || state == BottomSheetState.Collapsed) {
            bottomSheetScreenViewState.value = null
            bottomSheetOnDismiss?.invoke()
            bottomSheetOnDismiss = null
            deinitBottomSheetJob()
            clearBottomSheetViewModel()
        }
    }

    private fun onBottomSheetStateChange(stateChange: BottomSheetStateChange?) {
//        Log.i("onBottomSheetStateChange(): stateChange: $stateChange")
        if (stateChange == null) {
            return
        }

        val startColor = Color.Transparent
        val endColor = Color.Black.copy(alpha = 0.5f)

        val color = lerp(startColor, endColor, stateChange.openProgressFraction)
        bottomSheetScrimColor.value = if (color == Color.Transparent) {
            null
        } else {
            color
        }
    }

    val indexViewModel by lazy {
        IndexViewModel(
            appStateManager = this,
            contentRepository,
            viewModelFactory,
            onboardingManager,
            exploreRepository,
            accountManager,
            profileImageManager,
            messageBarManager,
            themeManager,
            systemNavigator,
            purchaseUiManager,
            viewStateRefresher,
            viewStateFactory,
            indexTabSpecFactory,
            viewStateFactoryHome,
            homeFilterManager,
            contentColorManager,
            contentCacheManager,
            contentPrefetcherFactory,
            appConfig,
            timeRepository,
            globalOverlayManager,
            accountDataRepository,
        )
    }

    override fun navigateBack() {
        Log.d("[Navigation] navigateBack()")
        if (isModalBottomSheetVisible) {
            Log.d("[Navigation] Dismissing modal bottom sheet")
            dismissModalBottomSheet { }
        } else if (isBottomSheetVisible) {
            Log.d("[Navigation] Dismissing bottom sheet")
            dismissBottomSheet()
        } else if (indexSearchInputOverlayIsVisible) {
            Log.d("[Navigation] Dismissing search input overlay")
            indexViewModel.hideSearchInputOverlay()
        } else if (indexSearchResultsIsVisible) {
            Log.d("[Navigation] Dismissing search results")
            indexViewModel.hideSearchResults()
        } else {
            Log.d("[Navigation] Popping screen")
            appNavigator.popScreen()
        }
    }

    override fun dismissSearch() {
        indexViewModel.hideSearchInputOverlay()
        indexViewModel.hideSearchResults()
    }

    override fun dismissSearchInput() {
        indexViewModel.hideSearchInputOverlay()
    }

    private val screenViewStates: Flow<List<ScreenViewState>> by lazy {
        MutableStateFlow(listOf(indexViewModel.viewState.value))
    }

    override val isUiReady: StateFlow<Boolean> by lazy {
        combine(
            viewSpecArbitrator.isReady,
            appNavigator.isReady,
        ) { viewSpecArbitratorReady, navigationReady ->
            viewSpecArbitratorReady && navigationReady
        }.stateIn(false)
    }

    override val mediaInitializationState: StateFlow<InitializationState>
        get() = mediaMapRepository.initializationState

    override val appViewState: StateFlow<AppViewState> by lazy {
        combine(
            indexViewModel.viewState,
            screenViewStates,
            bottomSheetViewState,
            modalBottomSheetViewState,
            indexViewModel.currentTab,
            indexViewModel.overlayScreenViewState,
            alertManager.currentDialog,
            globalOverlayManager.globalOverlayViewState,
            viewStateRefresher.refresh,
        ) { indexViewState,
            screenViewStates,
            bottomSheetScreenViewState,
            modalBottomSheetViewState,
            _,
            overlayScreenViewState,
            dialogViewState,
            globalOverlayState,
            _ ->
            AppViewStateBuilder.build(
                screenViewStates = screenViewStates,
                bottomSheetViewState = bottomSheetScreenViewState,
                modalBottomSheetViewState = modalBottomSheetViewState,
                homeScreenViewState = indexViewState,
                overlayScreenViewState = overlayScreenViewState,
                alertViewState = dialogViewState,
                globalOverlayViewState = globalOverlayState,
                onBack = { navigateBack() },
            )
        }.stateIn(AppViewStateBuilder.buildDefault(homeScreenViewState = indexViewModel.viewState.value))
    }

    private val navigationEventNoOp: Flow<NavigationEvent> = MutableStateFlow(NavigationEvent.NoOpEvent)
    override val navigationEvent: Flow<NavigationEvent>
        get() = if (appNavigator is AppNavigatorDefault) {
            appNavigator.navigationEvent
        } else {
            navigationEventNoOp
        }

    override fun navigateToRewardAd(rewardAdCallbacks: RewardAdCallbacks) {
        appNavigator.showRewardAd(rewardAdCallbacks)
    }

    override fun navigateToError(errorScreen: ErrorScreen) {
        navigateToScreen(ErrorScreenArgument(errorScreen))
    }

    override fun navigateToDebugSettings() {
        navigateToScreen(ScreenArgument.DebugSettingsScreenArgument)
    }

    private fun navigateToScreenAppNavigator(id: Id) {
        when (id) {
            is RemixId -> {
                navigateToScreen(WallpaperShowcaseScreenArgument(remixId = id))
            }

            is DesignId -> {
                throw IllegalArgumentException("Navigate to Design via ScreenArgument overload")
            }

            is ArtistId -> {
                navigateToScreen(ArtistIdScreenArgument(id))
            }

            is CategoryId -> {
                navigateToScreen(CollectionIdScreenArgument(id.collectionId, firstWallpaperId = null))
            }

            is CollectionId -> {
                navigateToScreen(CollectionIdScreenArgument(id, firstWallpaperId = null))
            }

            is FolderId -> {
                navigateToScreen(FolderScreenArgument(id))
            }

            is StoryCollectionId -> {
                TODO("Navigate to Stories via ScreenArgument overload")
//                navigateToScreen(Screen.Stories, argument = id.exportString)
            }

            is Id.StoryId -> {
                throw IllegalArgumentException("Navigate to Stories via ScreenArgument overload")
            }

            else -> {
                Log.w("Unhandled type in navigateToScreenAppNavigator(): ${id::class.simpleName}")
            }
        }
    }

    override fun navigateToScreen(id: Id) {
        navigateToScreenAppNavigator(id)
    }

    override fun navigateToScreen(arguments: ScreenArgument) {
        Log.d("[Navigation] navigateToScreen(arguments = $arguments)")

        when {
            arguments is CollectionActionScreenArgument -> {
                navigateToModalBottomSheet(arguments)
            }

            arguments is WallpaperSingleActionScreenArgument -> {
                navigateToModalBottomSheet(arguments)
            }

            arguments is ErrorScreenArgument
                    && arguments.errorScreen == ErrorScreen.AppUpdateRequired -> {
                appNavigator.toScreenBlockingUseWithExtremeCaution(arguments)
            }

            else -> {
                // Dismiss modal bottom sheet first, then apply transition. #1367
                if (isModalBottomSheetVisible && !PlatformFeature.SystemBottomSheetsSupported) {
                    dismissModalBottomSheet {
                        navigateToScreen(arguments, clearScreenStack = false)
                    }
                } else {
                    navigateToScreen(arguments, clearScreenStack = false)
                }
            }
        }
    }

    private fun navigateToScreen(
        screenArgument: ScreenArgument,
        clearScreenStack: Boolean,
    ) {
        appNavigator.toScreen(screenArgument, clearScreenStack)
    }

    private fun navigateToIndexAtRoot() {
        val currentScreen = appNavigator.currentScreen
        // Ensures app behaves correctly when exiting Onboarding. See #19
        if (currentScreen == Screen.Index) {
            return
        }

        navigateToScreen(IndexScreenArgument, clearScreenStack = true)
    }

    override fun navigateToIndexTab(indexTab: IndexTab) {
        indexViewModel.setCurrentTab(indexTab)
    }

    override fun navigateToSearch() {
        if (searchIsOverlay) {
            indexViewModel.showSearchInputOverlay()
        } else {
            navigateToScreen(SearchScreenArgument)
        }
    }

    private val searchIsOverlay: Boolean
        get() = appConfig.searchIsOverlay

    private val spotlightBottomSheet: Boolean
        get() = appConfig.spotlightBottomSheet.value

    override fun navigateToPaywall(
        autoTriggerPurchase: Boolean,
        subscriptionExpired: Boolean,
        subscriptionPlan: SubscriptionPlan?,
    ) {
        val argument = if (remotePaywallManager.supportsDynamicPaywall.value) {
            PaywallScreenArgument.Arbitrated(
                autoTriggerPurchase = autoTriggerPurchase,
                subscriptionExpired = subscriptionExpired,
                subscriptionPlan = subscriptionPlan,
            )
        } else {
            PaywallScreenArgument.Internal(
                autoTriggerPurchase = autoTriggerPurchase,
                subscriptionExpired = subscriptionExpired,
                subscriptionPlan = subscriptionPlan,
            )
        }
        navigateToScreen(argument)
    }

    override fun navigateToManageSubscription() {
        if (PlatformFeature.NativeManageSubscriptionSupported) {
            navigateToScreen(ScreenArgument.ManageSubscriptionScreenArgument)
        } else {
            navigateToPaywall()
        }
    }

    override fun navigateToOssLicenses() {
        navigateToScreen(OssLicensesScreenArgument)
    }

    override fun navigateToAccount() {
        navigateToScreen(AccountScreenArgument)
    }

    override fun navigateToArtists() {
        navigateToScreen(ArtistsScreenArgument)
    }

    override fun navigateToFirstRun() {
        navigateToScreen(ScreenArgument.FirstRunScreenArgument, clearScreenStack = true)
    }

    override fun navigateToSignUp() {
        navigateToScreen(SignUpScreenArgument, clearScreenStack = true)
    }

    override fun navigateToDataConsent() {
        navigateToScreen(DataConsentScreenArgument)
    }

    private var modalBottomSheetCurrentScreenViewStateJob: Job? = null
    private var modalBottomSheetCurrentViewStateProvider: BottomSheetViewStateProvider? = null
    private val modalBottomSheetOnDismiss: MutableStateFlow<(() -> Unit)?> = MutableStateFlow(null)
    private val modalBottomSheetRequestHidden: Flow<Boolean> = modalBottomSheetOnDismiss
        .map { it != null }

    private fun navigateToModalBottomSheet(
        screenArgument: ScreenArgument,
    ) {
        if (PlatformFeature.SystemBottomSheetsSupported) {
            navigateToScreen(screenArgument, clearScreenStack = false)
        } else {
            val provider = bottomSheetViewStateProviderManager.createBottomSheetScreenViewStateProvider(screenArgument)
            requireNotNull(provider) { "BottomSheetHolder must provide a ScreenViewStateProvider" }
            modalBottomSheetCurrentViewStateProvider = provider
            modalBottomSheetCurrentScreenViewStateJob?.cancel()
            modalBottomSheetCurrentScreenViewStateJob = viewModelScope.launch {
                combine(
                    provider.viewState, modalBottomSheetRequestHidden
                ) { screenViewState, requestHidden ->
                    ModalBottomSheetViewState(
                        screenViewState,
                        scrimColor = null,
                        shapeSpec = shapeSpecFactory.modalBottomSheetShapeSpec,
                        requestHideState = requestHidden,
                        onDismissed = modalBottomSheetOnDismissed,
                    )
                }.collect {
                    modalBottomSheetViewState.value = it
                }
            }
        }
    }

    private fun resetModalBottomSheet(via: String) {
        if (modalBottomSheetViewState.value == null
            && modalBottomSheetCurrentScreenViewStateJob == null
            && modalBottomSheetCurrentViewStateProvider == null) {
            return
        }

        Log.w("[Navigation] resetModalBottomSheet() via $via")
        modalBottomSheetViewState.value = null
        modalBottomSheetCurrentScreenViewStateJob?.cancel()
        modalBottomSheetCurrentScreenViewStateJob = null
        modalBottomSheetCurrentViewStateProvider?.destroy()
        modalBottomSheetCurrentViewStateProvider = null

        val onDismiss = modalBottomSheetOnDismiss.value
        if (onDismiss != null) {
            onDismiss()
        }
        modalBottomSheetOnDismiss.value = null
    }

    override fun dismissModalBottomSheet(onDismiss: () -> Unit) {
        modalBottomSheetOnDismiss.value = onDismiss
    }

    private val modalBottomSheetOnDismissed: () -> Unit = {
        resetModalBottomSheet("modalBottomSheetOnDismissed")
    }

    private var bottomSheetViewModel: ViewModel? = null

    private fun createBottomSheetViewState(
        screenViewState: ScreenViewState?,
        scrimColor: Color?,
    ): BottomSheetViewState? {
        if (screenViewState == null && scrimColor == null) {
            return null
        }

        return BottomSheetViewState(screenViewState, scrimColor).also {
            Log.d("createBottomSheetViewState(): scrimColor: ${it.scrimColor}")
        }
    }

    private fun clearBottomSheetViewModel() {
        bottomSheetViewModel?.also {
            it.onCleared()
            bottomSheetViewModel = null
        }
    }

    private fun navigateToBottomSheet(
        viewModel: ViewModel,
    ) {
        clearBottomSheetViewModel()

        require(viewModel is ScreenViewStateProvider) { "ViewModel ${viewModel::class} must implement ScreenViewStateProvider"}
        bottomSheetViewModel = viewModel

        openBottomSheet(viewModel.viewState)
    }

    private fun openBottomSheet(viewState: StateFlow<ScreenViewState?>) {
        deinitBottomSheetJob()
        bottomSheetManager?.setState(BottomSheetState.Expanded)
        bottomSheetViewStateJob = viewState.collectIn(viewModelScope) {
            bottomSheetScreenViewState.value = it
        }
    }

    private fun deinitBottomSheetJob() {
        bottomSheetViewStateJob?.apply {
            cancel()
            bottomSheetViewStateJob = null
        }
    }

    private var bottomSheetOnDismiss: (() -> Unit)? = null
    override fun dismissBottomSheet(onDismiss: (() -> Unit)?) {
        bottomSheetOnDismiss = onDismiss
        bottomSheetManager?.setState(BottomSheetState.Collapsed)
        bottomSheetScreenViewState.value = null
    }

    private fun routeToDynamicRoot(
        navigationIsReady: Boolean = appNavigator.isReady.value,
        targetOnboardingState: OnboardingState? = onboardingManager.targetOnboardingState.value,
        firstRunOnboardingDismissed: Boolean = appState.firstRunOnboardingDismissed.value,
    ) {
        if (!navigationIsReady) {
            return
        }
        Log.d("targetOnboardingState: $targetOnboardingState, firstRunOnboardingDismissed: $firstRunOnboardingDismissed")
        if (firstRunOnboardingDismissed) {
            navigateToIndexAtRoot()
        }
    }

    /**
     * Hopefully short-lived variable to help initialize Precompose to the correct screen.
     * See #4.
     */
    val initialScreen: Screen
        get() {
            val firstRunOnboardingDismissed = appState.firstRunOnboardingDismissed.value
            return if (firstRunOnboardingDismissed) {
                Screen.Index
            } else {
                Screen.FirstRun
            }
        }

    override fun onCleared() {
        indexViewModel.onCleared()
        deinitBottomSheetJob()
    }

    init {
        // Sanity check to ensure not removed by Proguard
        entitlementManager

        if (appStateManager is AppStateManagerWrapper) {
            appStateManager.setAppViewModel(this)
        }

        appNavigator.isReady.collectIn(coroutineScopeMain) {
            routeToDynamicRoot(navigationIsReady = it)
        }
        onboardingManager.targetOnboardingState.collectIn(coroutineScopeMain) {
            routeToDynamicRoot(targetOnboardingState = it)
        }
        appState.firstRunOnboardingDismissed.collectIn(coroutineScopeMain) {
            routeToDynamicRoot(firstRunOnboardingDismissed = it)
        }

        isUiReady.collectIn(coroutineScopeMain) {
            if (it) {
                appLaunchManager.run()
            }
        }

        combine(
            inAppUpdateChecker.appUpdateRequired,
            isUiReady,
        ) { appUpdateRequired, isUiReady ->
            appUpdateRequired && isUiReady
        }
            .collectIn(coroutineScopeMain) {
                if (it) {
                    navigateToScreen(
                        ErrorScreenArgument(ErrorScreen.AppUpdateRequired),
                    )
                }
            }

        Log.w("[Navigation] AppViewModel initialized")
    }
}