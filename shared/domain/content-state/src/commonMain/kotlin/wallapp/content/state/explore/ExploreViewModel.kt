package wallapp.content.state.explore

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.app.AppStateManager
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.ContentState
import wallapp.content.state.ContentStateFeed
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.paging.ContentStatePagingController
import wallapp.coroutine.collectIn
import wallapp.data.content.ContentCacheManager
import wallapp.data.highlight.Highlight
import wallapp.graphics.Color
import wallapp.log.Log
import wallapp.pixel.feed.FeedScrollPosition
import wallapp.pixel.feed.FeedScrollPositionUpdateSink
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.theme.ThemeManager
import wallapp.time.TimeRepository
import wallapp.util.combine
import wallapp.util.simpleClassName
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel


class ExploreViewModel(
    private val appStateManager: AppStateManager,
    private val exploreRepository: ExploreRepository,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val contentCacheManager: ContentCacheManager,
    private val contentColorManager: ContentColorManager,
    private val themeManager: ThemeManager,
    private val viewStateFactory: ViewStateFactory,
    private val viewStateRefresher: ViewStateRefresher,
    timeRepository: TimeRepository,
) : ViewModel(), ScreenSystemBarControllerHolder, ScreenViewStateProvider {

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        combine(
            currentHighlight,
            themeManager.theme,
            statusBarBackgroundAlpha,
            viewStateRefresher.refresh,
        ) { currentHighlight, theme, statusBarBackgroundAlpha, _ ->
            if (statusBarBackgroundAlpha != null
                && statusBarBackgroundAlpha < .6f
                && currentHighlight != null) {
                ScreenSystemBarController.TranslucentStatusBar(
                    currentHighlight.useDarkStatusBarIcons,
                )
            } else {
                ScreenSystemBarController.Dynamic(theme.themeColors.surface)
            }
        }
            .stateIn(ScreenSystemBarController.Default)
    }

    private var currentScrollPosition: FeedScrollPosition = FeedScrollPosition.Unknown
    private var lastFeedOffset: Float? = null

    private val feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink = { scrollPosition ->
        currentScrollPosition = scrollPosition
    }

    private val lastFeedOffsetUpdateSink: LastFeedOffsetUpdateSink = { event ->
        lastFeedOffset = event.lastFeedOffset
    }

    private val scrollStateController = FeedScrollStateController(viewModelScope)

    private val indexStatusBarContainer: StateFlow<Color>
        get() = contentColorManager.indexStatusBarContainer

    private val statusBarBackgroundAlpha = MutableStateFlow<Float?>(null)
    private val statusBarBackgroundAlphaOnChange: (Float) -> Unit = { alpha ->
        statusBarBackgroundAlpha.value = alpha
    }

    private val highlightsCurrentIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    private val highlightsOnPageChange: (Int) -> Unit = { page ->
        highlightsCurrentIndex.value = page
    }

    private val currentHighlight: Flow<Highlight?> = combine(
        exploreRepository.contentStateFeed,
        highlightsCurrentIndex,
    ) { contentStateFeed, index ->
        if (index == -1) {
            return@combine null
        }
        contentStateFeed
            ?.feed
            ?.filterIsInstance<ContentState.Highlights>()
            ?.firstOrNull()
            ?.highlights
            ?.highlights
            ?.getOrNull(index)
    }

    private val refreshRateLimiter = ExploreRefreshRateLimiter(timeRepository)

    private val contentPrefetcher: ContentPrefetcher by lazy {
        contentPrefetcherFactory.createContentPrefetcher(
            screenId = this.simpleClassName,
            coroutineScope = viewModelScope,
        )
    }

    private val contentStatePagingController: ContentStatePagingController
        get() = exploreRepository.contentStatePagingController

    private fun createViewState(
        isUiReady: Boolean,
        data: ContentStateFeed?,
        scrollToTop: MutableSharedFlow<Unit>,
        statusBarColor: Color = indexStatusBarContainer.value,
    ): ExploreViewState {
        if (!isUiReady) {
            return ExploreViewState.Loading
        }
        return viewStateFactory.createExploreFeedViewState(
            contentStateFeed = data,
            pagingViewEventSink = contentStatePagingController.pagingViewEventSink,
            contentPrefetcher = contentPrefetcher,
            wallpaperViewsPagingController = contentStatePagingController,
            scrollToTop = scrollToTop,
            scrollStateWrapper = scrollStateController.scrollStateWrapper,
            statusBarColor = statusBarColor,
            feedScrollPositionUpdateSink = feedScrollPositionUpdateSink,
            lastFeedOffsetUpdateSink = lastFeedOffsetUpdateSink,
            feedOffset = lastFeedOffset,
            statusBarBackgroundAlphaOnChange = statusBarBackgroundAlphaOnChange,
            highlightsOnPageChange = highlightsOnPageChange,
        )
    }

    private val scrollToTop: MutableSharedFlow<Unit> = MutableSharedFlow()
    fun triggerScrollToTop() {
        if (currentScrollPosition == FeedScrollPosition.Top) {
            if (refreshRateLimiter.isRefreshAllowed()) {
                viewStateRefresher.globalRefresh()
            } else {
                Log.i("ExploreViewModel - Refresh is limited")
            }
            return
        }
        viewModelScope.launch {
            scrollToTop.emit(Unit)
        }
    }

    private val data: Flow<ContentStateFeed?>
        get() = exploreRepository.contentStateFeed

    @FlowInterop.Enabled
    override val viewState: StateFlow<ExploreViewState> = combine(
        appStateManager.isUiReady,
        data,
        indexStatusBarContainer,
        contentStatePagingController.loadMoreTrigger,
        scrollStateController.lastScrollStateUpdateFlow,
        viewStateRefresher.refresh,
    ) { isUiReady, data: ContentStateFeed?, statusBarColor, _, _, _ ->
        createViewState(isUiReady, data, scrollToTop, statusBarColor)
    }
        .stateIn(createViewState(isUiReady = false, null, scrollToTop), startWhileSubscribedNetwork = true)

    private fun prefetchData(data: ContentStateFeed) {
        data.feed
            .filterIsInstance<ContentState.Highlights>()
            .ifEmpty { null }
            ?.also { highlights ->
                contentCacheManager.prefetchHighlights(highlights.first().highlights)
            }
    }

    init {
        data.collectIn(viewModelScope) { data ->
            data?.also {
                prefetchData(it)
            }
        }
    }
}