package wallapp.content.state.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import wallapp.account.Account
import wallapp.account.AccountManager
import wallapp.app.AppStateManager
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.pager.PagerPersistableStateController
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.HomePagedContentResult
import wallapp.data.home.HomeContentType
import wallapp.graphics.Color
import wallapp.image.Image
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.profileimage.ProfileImageManager
import wallapp.util.combine
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel


class HomeViewModel(
    private val appStateManager: AppStateManager,
    private val contentRepository: ContentRepository,
    accountManager: AccountManager,
    private val profileImageManager: ProfileImageManager,
    messageBarManager: MessageBarManager,
    private val homeViewStateFactory: HomeViewStateFactory,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val contentColorManager: ContentColorManager,
    homeFilterManager: HomeFilterManager,
    viewStateRefresher: ViewStateRefresher,
): ViewModel(), ScreenViewStateProvider {

    private val homeContentType: StateFlow<HomeContentType> =
        homeFilterManager.homeContentType
            .stateIn(initialValue = HomeContentType.Suggested)
    private val initialTabIndex: Int by lazy {
        when (homeContentType.value) {
            HomeContentType.Suggested -> 0
            HomeContentType.Liked -> 1
            HomeContentType.Purchased -> 2
        }
    }

    private val homePagedContent: Flow<HomePagedContentResult?>
        get() = contentRepository.homePagedContent
    private val topBarContainerColor: StateFlow<Color>
        get() = contentColorManager.topBarContainer

    private val scrollToTop: MutableSharedFlow<Unit> = MutableSharedFlow()

    private val scrollStateControllers = mapOf(
        HomeContentType.Suggested to FeedScrollStateController(viewModelScope),
        HomeContentType.Liked to FeedScrollStateController(viewModelScope),
        HomeContentType.Purchased to FeedScrollStateController(viewModelScope),
    )
    private val scrollStateWrappers: Map<HomeContentType, FeedScrollStateWrapper>
        get() = scrollStateControllers.mapValues { it.value.scrollStateWrapper }
    private val scrollStateFlow = merge(
        *scrollStateControllers.map { it.value.lastScrollStateUpdateFlow }.toTypedArray()
    )

    private val pagerPersistableStateController = PagerPersistableStateController(viewModelScope)

    private val contentPrefetchers: List<ContentPrefetcher> by lazy {
        listOf(
            contentPrefetcherFactory.createContentPrefetcher(
                screenId = "Home-Tab0",
                coroutineScope = viewModelScope,
            ),
            contentPrefetcherFactory.createContentPrefetcher(
                screenId = "Home-Tab1",
                coroutineScope = viewModelScope,
            ),
            contentPrefetcherFactory.createContentPrefetcher(
                screenId = "Home-Tab2",
                coroutineScope = viewModelScope,
            ),
        )
    }

    fun triggerScrollToTop() {
        viewModelScope.launch {
            scrollToTop.emit(Unit)
        }
    }

    private fun createViewState(
        isUiReady: Boolean,
        homePagedContentResult: HomePagedContentResult? = null,
        profileImage: Image = profileImageManager.profileImage.value,
        account: Account? = null,
        messageBar: MessageBarViewState? = null,
        topBarContainerColor: Color = this.topBarContainerColor.value,
        scrollToTop: MutableSharedFlow<Unit> = this.scrollToTop,
    ): HomeViewState {
        if (!isUiReady) return HomeViewState.Loading

        return homeViewStateFactory.createHomePagedViewState(
            homePagedContentResult = homePagedContentResult,
            profileImage = profileImage,
            hasAccount = account != null,
            messageBar = messageBar,
            topBarContainerColor = topBarContainerColor,
            contentPrefetchers = contentPrefetchers,
            scrollToTop = scrollToTop,
            initialTabIndex = pagerPersistableStateController.pagerPersistableStateWrapper.lastPagerState?.initialPage ?: initialTabIndex,
            scrollStateWrappers = scrollStateWrappers,
            lastPagerStateUpdateSink = pagerPersistableStateController.pagerPersistableStateWrapper.lastPagerStateUpdateSink,
        )
    }

    override val viewState: StateFlow<HomeViewState> = combine(
        appStateManager.isUiReady,
        homeContentType,
        homePagedContent,
        profileImageManager.profileImage,
        accountManager.signedInAccount,
        messageBarManager.messageBarViewState,
        topBarContainerColor,
        viewStateRefresher.refresh,
        scrollStateFlow,
        pagerPersistableStateController.lastPagerStateUpdateFlow,
    ) { isUiReady, _, homeContentType, profileImage, account, messageBar, topBarContainerColor, _, _, _ ->
        createViewState(isUiReady, homeContentType, profileImage, account, messageBar, topBarContainerColor)
    }.stateIn(createViewState(isUiReady = false), startWhileSubscribedNetwork = true)
}