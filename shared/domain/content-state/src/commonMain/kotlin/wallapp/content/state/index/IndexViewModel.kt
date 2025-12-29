package wallapp.content.state.index

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.account.state.signin.SignInViewModel
import wallapp.app.AppStateManager
import wallapp.appconfig.AppConfig
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.explore.ExploreRepository
import wallapp.content.state.explore.ExploreViewModel
import wallapp.content.state.home.HomeFilterManager
import wallapp.content.state.home.HomeOnboardingViewModel
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.content.state.home.HomeViewModel
import wallapp.content.state.home.HomeViewState
import wallapp.content.state.home.HomeViewStateFactory
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.profile.ProfileViewModel
import wallapp.content.state.search.SearchInputViewModel
import wallapp.content.state.search.SearchResultsViewModel
import wallapp.content.state.search.SearchResultsViewState
import wallapp.content.state.search.stateOrNull
import wallapp.data.content.ContentCacheManager
import wallapp.data.content.ContentRepository
import wallapp.graphics.Color
import wallapp.log.Log
import wallapp.onboarding.OnboardingManager
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.navigationbar.NavigationBarItem
import wallapp.pixel.navigationbar.NavigationBarViewState
import wallapp.pixel.pager.PagerEventTracker
import wallapp.pixel.pager.PagerState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.profileimage.ProfileImageManager
import wallapp.purchase.PurchaseUiManager
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.system.navigation.SystemNavigator
import wallapp.theme.ThemeManager
import wallapp.time.TimeRepository
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel
import wallapp.viewmodel.ViewModelFactory


class IndexViewModel(
    private val appStateManager: AppStateManager,
    private val contentRepository: ContentRepository,
    private val viewModelFactory: ViewModelFactory,
    onboardingManager: OnboardingManager,
    exploreRepository: ExploreRepository,
    accountManager: AccountManager,
    profileImageManager: ProfileImageManager,
    private val messageBarManager: MessageBarManager,
    private val themeManager: ThemeManager,
    private val systemNavigator: SystemNavigator,
    purchaseUiManager: PurchaseUiManager,
    viewStateRefresher: ViewStateRefresher,
    private val viewStateFactory: ViewStateFactory,
    private val indexTabSpecFactory: IndexTabSpecFactory,
    private val homeViewStateFactory: HomeViewStateFactory,
    private val homeFilterManager: HomeFilterManager,
    private val contentColorManager: ContentColorManager,
    private val contentCacheManager: ContentCacheManager,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val appConfig: AppConfig,
    private val timeRepository: TimeRepository,
    private val globalOverlayManager: GlobalOverlayManager,
    private val accountDataRepository: AccountDataRepository,
) : ViewModel(), ScreenSystemBarControllerHolder, ScreenViewStateProvider {

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        combine(
            currentTab,
            currentScreenViewState,
            showHomeOnboardingSuccess,
            themeManager.theme,
            exploreViewModel.screenSystemBarController,
        ) { currentTab, currentScreenViewState, showHomeOnboardingSuccess, theme, exploreController ->
            if (showHomeOnboardingSuccess) {
                ScreenSystemBarController.DefaultOpposite
            } else if (currentTab == IndexTab.Home && currentScreenViewState is HomeViewState.Data) {
                ScreenSystemBarController.Dynamic(currentScreenViewState.topBar.containerColor.copy(alpha = 1f))
            } else if (currentTab == IndexTab.Explore) {
                exploreController
            } else if (currentTab == IndexTab.Account) {
                ScreenSystemBarController.Dynamic(theme.themeColors.surface)
            } else {
                ScreenSystemBarController.Default
            }
        }.stateIn(ScreenSystemBarController.Default)
    }

    private val usePager: StateFlow<Boolean>
        get() = appConfig.pagedIndexScreen

//    private val collectionsViewModel by lazy {
//        CollectionsViewModel(
//            contentRepository,
//            viewStateRefresher,
//            viewFactory,
//            feedFormatter,
//        )
//    }
    val exploreViewModel by lazy {
        ExploreViewModel(
            appStateManager,
            exploreRepository,
            contentPrefetcherFactory,
            contentCacheManager,
            contentColorManager,
            themeManager,
            viewStateFactory,
            viewStateRefresher,
            timeRepository,
        )
    }
    val homeOnboardingViewModel by lazy {
        HomeOnboardingViewModel(
            appStateManager,
            contentRepository,
            contentCacheManager,
            viewStateFactory,
            themeManager,
            onboardingManager,
            globalOverlayManager,
        )
    }
    val homeViewModel by lazy {
//        HomeViewModelFiltered(
        HomeViewModel(
            contentRepository = contentRepository,
            accountManager = accountManager,
            profileImageManager = profileImageManager,
            messageBarManager = messageBarManager,
            homeViewStateFactory = homeViewStateFactory,
            contentPrefetcherFactory = contentPrefetcherFactory,
            contentColorManager = contentColorManager,
            homeFilterManager = homeFilterManager,
            appStateManager = appStateManager,
            viewStateRefresher = viewStateRefresher,
        )
    }
    val profileViewModel by lazy {
        ProfileViewModel(
            appStateManager = appStateManager,
            contentRepository = contentRepository,
            accountManager = accountManager,
            profileImageManager = profileImageManager,
            signInViewModel = signInViewModel,
            viewStateFactory = viewStateFactory,
            purchaseUiManager = purchaseUiManager,
            viewStateRefresher = viewStateRefresher,
            contentColorManager = contentColorManager,
            messageBarManager = messageBarManager,
            systemNavigator = systemNavigator,
            accountDataRepository = accountDataRepository,
        )
    }
    private val signInViewModel by lazy {
        viewModelFactory.create(SignInViewModel::class)
    }
    val searchResultsViewModel by lazy {
        viewModelFactory.create(SearchResultsViewModel::class)
    }
    private val searchInputViewModel by lazy {
        viewModelFactory.create(SearchInputViewModel::class)
    }

    private val showHomeOnboardingSuccess: StateFlow<Boolean> by lazy {
        combine(
            currentTab,
            showHomeOnboarding,
            currentScreenViewState,
        ) { currentTab, showHomeOnboarding, currentScreenViewState ->
            currentTab == IndexTab.Home
                    && showHomeOnboarding
                    && currentScreenViewState is HomeOnboardingViewState.Success
        }.stateIn(initialValue = false)
    }

    // Kind of a hack to create a one time trigger for home onboarding state change so that it is
    // only set once per change and set to null after consuming it
    private val showHomeOnBoardingChanged = MutableStateFlow<Boolean?>(null)
    private var isLastPageChangeManual = false

    private val showHomeOnboarding: StateFlow<Boolean> =
        onboardingManager.homeOnboardingFinished
            .map { homeOnboardingFinished ->
                val showHomeOnboarding = !homeOnboardingFinished
                if (showHomeOnboarding && !isLastPageChangeManual) {
                    setCurrentTab(IndexTab.Home, isManualPageChange = false)
                } else if (homeOnboardingFinished) {
                    if (currentTab.value == IndexTab.Account) {
                        setCurrentTab(IndexTab.Account)
                    } else {
                        setCurrentTab(IndexTab.Explore)
                    }
                }
                showHomeOnboarding
            }
            .scan(false) { prev, curr ->
                if (curr != prev) {
                    showHomeOnBoardingChanged.value = curr
                }
                curr
            }
            .stateIn(initialValue = false)

    private val defaultViewState: ScreenViewState
        get() = exploreViewModel.viewState.value
    private val _currentTab = MutableStateFlow(IndexTab.Explore)

    @FlowInterop.Enabled
    val currentTab: StateFlow<IndexTab>
        get() = _currentTab

    fun setCurrentTab(indexTab: IndexTab) {
        setCurrentTab(indexTab, isManualPageChange = true)
    }

    private fun setCurrentTab(indexTab: IndexTab, isManualPageChange: Boolean) {
        Log.d("setCurrentTab($indexTab), previous: ${_currentTab.value}, isManualPageChange: $isManualPageChange")

        if (indexTab == _currentTab.value) {
            onCurrentTabReselected()
            return
        }

        _currentTab.value = indexTab
        this.isLastPageChangeManual = true
        if (isManualPageChange) {
            pagerEventTracker.registerManualPageChange(indexTab.index)
        }
    }

    @FlowInterop.Enabled
    val showSearchResults: StateFlow<Boolean> by lazy {
        combine(
            currentTab,
            showSearchInputOverlay,
            searchResultsViewModel.viewState,
        ) { currentTab, _, searchResultsViewState ->
            val isShowSearchResultsViewState = searchResultsViewState is SearchResultsViewState.Data
            currentTab == IndexTab.Explore && isShowSearchResultsViewState
        }.stateIn(initialValue = false)
    }

    private val searchResultsViewState: Flow<SearchResultsViewState?> by lazy {
        combine(
            showSearchResults,
            searchResultsViewModel.viewState,
        ) { showSearchResults, searchResultsViewState ->
            if (showSearchResults) {
                searchResultsViewState
            } else {
                null
            }
        }
    }

    private val showSearchInputOverlay = MutableStateFlow(false)
    val searchInputOverlayIsVisible: StateFlow<Boolean>
        get() = showSearchInputOverlay

    private val searchOverlayScreenViewState: StateFlow<ScreenViewState?>
        get() = searchInputViewModel.viewState

    val overlayScreenViewState: Flow<ScreenViewState?>
        get() = searchOverlayScreenViewState
    private val overlayProgress: StateFlow<Float>
        get() = searchInputViewModel.overlayProgress
    private val onOverlayProgressChange: (Float) -> Unit = { progress ->
        searchInputViewModel.onOverlayVisibilityProgress(progress)
    }
    private val onOverlayDismissed: () -> Unit = {
        showSearchInputOverlay.value = false
        searchInputViewModel.onSearchInputOverlayDismissed()
    }

    fun showSearchInputOverlay() {
        if (!showSearchInputOverlay.value) {
            searchInputViewModel.onOverlayVisibilityProgress(0f)
        }
        showSearchInputOverlay.value = true
        searchResultsViewModel.show()
    }
    fun hideSearchInputOverlay() {
        showSearchInputOverlay.value = false
    }

    private val pagerEventTrackerListener = object : PagerEventTracker.Listener {

        override fun onCurrentPageChanged(currentPage: Int, isManualPageChange: Boolean) {
//            Log.d("onCurrentPageChanged() $currentPage, isManualPageChange: $isManualPageChange")
            if (!isManualPageChange) {
                setCurrentTab(indexTabAtIndex(currentPage), isManualPageChange = false)
            }
        }
    }

    private val pagerEventTracker = PagerEventTracker(pagerEventTrackerListener)

    private val onPagerStateChanged: (PagerState) -> Unit = { pagerState ->
//        Log.d("onPagerStateChanged: $pagerState")
        pagerEventTracker.add(pagerState)
    }

    fun onCurrentTabReselected() {
        when (currentTab.value) {
            IndexTab.Home -> { if (!showHomeOnboarding.value) homeViewModel.triggerScrollToTop() }
            IndexTab.Explore -> if (showSearchResults.value) searchResultsViewModel.triggerScrollToTop() else exploreViewModel.triggerScrollToTop()
            IndexTab.Account -> profileViewModel.triggerScrollToTop()
        }
    }

    private val navigationBarContainerColor: StateFlow<Color>
        get() = contentColorManager.indexBottomBarContainer
    private val navigationBarForceShow: StateFlow<Boolean?> = combine(
        showSearchInputOverlay,
        overlayProgress,
        showHomeOnBoardingChanged,
    ) { showSearchOverlay, overlayProgress, showHomeOnboarding ->
        if (showSearchOverlay) {
            true
        } else if (overlayProgress > 0f) {
            false
        } else if (showHomeOnboarding == false) {
            false
        } else {
            null
        }
    }.onEach {
        viewModelScope.launch {
            // Need a delay here otherwise the value for navigationBarForceShow will be reset
            // immediately based on the showHomeOnBoardingChanged.value == null
            delay(500)
            showHomeOnBoardingChanged.value = null
        }
    }.stateIn(null)

    // Monitor [signedInAccount] for current account change
    private val navigationBarViewState: StateFlow<NavigationBarViewState?> =
        combine(
            appStateManager.isUiReady,
            navigationBarContainerColor,
            overlayProgress,
            navigationBarForceShow,
            currentTab,
            accountManager.signedInAccount,
        ) { isUiReady, containerColor, overlayProgress, forceShow, _, _ ->
            createNavigationBarItems(isUiReady, containerColor, forceShow = forceShow, overlayProgress)
        }.stateIn(createNavigationBarItems(isUiReady = false))

    private val currentScreenViewState: StateFlow<ScreenViewState> by lazy {
        combine(
            currentTab,
            exploreViewModel.viewState,
            showHomeOnboarding,
            homeViewModel.viewState,
            homeOnboardingViewModel.viewState,
            searchResultsViewState,
            profileViewModel.viewState,
        ) { currentTab, explore, showHomeOnboarding, home, homeOnboarding, searchResults, profile ->
            when (currentTab) {
                IndexTab.Home -> if (showHomeOnboarding) { homeOnboarding } else { home }
                IndexTab.Explore -> searchResults?.stateOrNull() ?: explore
                IndexTab.Account -> profile
//            IndexTab.Categories -> collections
            }
        }.stateIn(exploreViewModel.viewState.value)
    }

    private val screenViewStates: StateFlow<List<ScreenViewState>> = combine(
        currentTab,
        showHomeOnboarding,
        exploreViewModel.viewState,
//        collectorsViewModel.viewState,
//        collectionsViewModel.viewState,
        homeViewModel.viewState,
        homeOnboardingViewModel.viewState,
        searchResultsViewState,
        profileViewModel.viewState,
    ) { _, showHomeOnboarding, explore, home, homeOnboarding, searchResultsViewState, profile ->
        listOf(
            if (showHomeOnboarding) { homeOnboarding } else { home },
            searchResultsViewState?.stateOrNull() ?: explore,
            profile,
        )
    }.stateIn(emptyList())

    private val IndexTab.index: Int
        get() = when (this) {
            IndexTab.Home -> 0
            IndexTab.Explore -> 1
            IndexTab.Account -> 2
        }
    private fun indexTabAtIndex(index: Int): IndexTab =
        when (index) {
            0 -> IndexTab.Home
            1 -> IndexTab.Explore
            2 -> IndexTab.Account
            else -> IndexTab.Explore
        }

    private fun createViewState(
        isUiReady: Boolean,
        screenViewStates: List<ScreenViewState> = emptyList(),
        overlayScreenViewState: ScreenViewState? = null,
        showSearchOverlay: Boolean = this.showSearchInputOverlay.value,
        currentTab: IndexTab = IndexTab.Explore,
        currentScreenViewState: ScreenViewState = defaultViewState,
        navigationBarViewState: NavigationBarViewState? = this.navigationBarViewState.value,
        usePager: Boolean = this.usePager.value
    ): IndexViewState {
        if (!isUiReady || screenViewStates.isEmpty() || navigationBarViewState == null) {
            return IndexViewState.Loading
        }

        return IndexViewState.Success(
            screens = screenViewStates,
            overlayScreen = overlayScreenViewState,
            overlayVisible = showSearchOverlay,
            onOverlayVisibilityProgress = onOverlayProgressChange,
            onOverlayDismissed = onOverlayDismissed,
            currentScreenIndex = currentTab.index,
            currentScreenViewState = currentScreenViewState,
            navigationBar = navigationBarViewState,
            scrollableTopBarHeight = currentScreenViewState.indexScrollableTopBarHeight,
            usePager = usePager,
            onPagerStateChange = onPagerStateChanged,
        )
    }

    private val _viewState: StateFlow<IndexViewState> = combine(
        appStateManager.isUiReady,
        screenViewStates,
        currentTab,
        currentScreenViewState,
        navigationBarViewState,
        overlayScreenViewState,
        showSearchInputOverlay,
        usePager,
        viewStateRefresher.refresh,
    ) { isUiReady, screenViewStates, currentTab, currentScreenViewState, navigationBarViewState, overlayScreenViewState, showSearchOverlay, usePager, _ ->
        createViewState(
            isUiReady = isUiReady,
            screenViewStates = screenViewStates,
            overlayScreenViewState = overlayScreenViewState,
            showSearchOverlay = showSearchOverlay,
            currentTab = currentTab,
            currentScreenViewState = currentScreenViewState,
            navigationBarViewState = navigationBarViewState,
            usePager = usePager,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = createViewState(isUiReady = false),
    )

    @FlowInterop.Enabled
    override val viewState: StateFlow<IndexViewState>
        get() = _viewState

    private val navBarItemOnClick: (IndexTab) -> Unit = {
        setCurrentTab(it, isManualPageChange = true)
    }

    private fun createNavBarItem(indexTab: IndexTab): NavigationBarItem {
        val tabSpec = indexTabSpecFactory.create(indexTab)
        return NavigationBarItem(
            isSelected = indexTab == currentTab.value,
            onClick = { navBarItemOnClick.invoke(indexTab) },
            text = tabSpec.label,
            selectedImage = tabSpec.selectedIcon,
            unselectedImage = tabSpec.unselectedIcon,
            imageShapeSpec = tabSpec.imageShapeSpec,
        )
    }

    private fun createNavigationBarItems(
        isUiReady: Boolean,
        containerColor: Color = navigationBarContainerColor.value,
        forceShow: Boolean? = navigationBarForceShow.value,
        overlayProgress: Float = 0f,
    ): NavigationBarViewState? {
        if (!isUiReady) return null
        return viewStateFactory.createIndexNavigationBar(
            items = listOf(
                createNavBarItem(IndexTab.Home),
                createNavBarItem(IndexTab.Explore),
                createNavBarItem(IndexTab.Account),
            ),
            containerColor = containerColor,
            offsetProgress = overlayProgress,
            forceShow = forceShow,
        )
    }

    override fun onCleared() {
        exploreViewModel.onCleared()
//        collectorsViewModel.onCleared()
//        collectionsViewModel.onCleared()
        homeViewModel.onCleared()
        homeOnboardingViewModel.onCleared()
        profileViewModel.onCleared()
        signInViewModel.onCleared()
        searchResultsViewModel.onCleared()
        searchInputViewModel.onCleared()
    }

    fun hideSearchResults() {
        searchResultsViewModel.hide()
    }
}