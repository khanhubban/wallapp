package wallapp.content.state.search

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import wallapp.app.AppStateManager
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.error.ErrorScreen
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.data.content.ContentRepository
import wallapp.network.NetworkRefreshManager
import wallapp.pixel.feed.FeedScrollPosition
import wallapp.pixel.feed.FeedScrollPositionUpdateSink
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.search.SearchResult
import wallapp.search.SearchSessionManager
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class SearchResultsViewModel(
    private val searchSessionManager: SearchSessionManager,
    private val viewStateFactory: ViewStateFactory,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val contentRepository: ContentRepository,
    private val appStateManager: AppStateManager,
    private val networkRefreshManager: NetworkRefreshManager,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    private var currentScrollPosition: FeedScrollPosition = FeedScrollPosition.Unknown

    private val feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink = { scrollPosition ->
        currentScrollPosition = scrollPosition
    }

    private val scrollToTop: MutableSharedFlow<Unit> = MutableSharedFlow()

    private val eventSink: (SearchResultsViewEvent) -> Unit = { event ->
        when (event) {
            is SearchResultsViewEvent.CloseSearch -> {
                hide()
            }
        }
    }

    fun triggerScrollToTop() {
        if (currentScrollPosition == FeedScrollPosition.Top) {
            hide()
            return
        } else {
            scrollToTop()
        }
    }

    private fun scrollToTop() {
        viewModelScope.launch {
            scrollToTop.emit(Unit)
        }
    }

    private val isVisible = MutableStateFlow(false)

    fun show() {
        isVisible.value = true
    }

    fun hide() {
        isVisible.value = false

        // This is a bit messy, but we need to delay the reset to ensure the view is hidden before
        // resetting the input data. See #1615.
        viewModelScope.launch {
            delay(200)
            searchSessionManager.resetAll()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val eventHandlerCloseSearch = ViewEventHandler.Event(
        eventSink = eventSink as ViewEventSink,
        event = SearchResultsViewEvent.CloseSearch,
    )

    private val contentPrefetcher: ContentPrefetcher by lazy {
        contentPrefetcherFactory.createContentPrefetcher(
            screenId = "SearchResults",
            coroutineScope = viewModelScope,
        )
    }

    private val searchResults = searchSessionManager.searchResults
        .scan<SearchResult, SearchResult>(SearchResult.Inactive) { previous, current ->
            if (previous is SearchResult.Results && current is SearchResult.Loading) {
                previous
            } else {
                current
            }
        }

    override val viewState: StateFlow<SearchResultsViewState> = combine(
        appStateManager.isUiReady,
        isVisible,
        searchResults,
        contentRepository.suggestedCollections,
        searchSessionManager.searchQuery,
        searchSessionManager.selectedFilters.onEach { scrollToTop() },
        networkRefreshManager.searchDataLoaded,
        viewStateRefresher.refresh,
    ) { isUiReady, isVisible, searchResults, suggestedCollections, searchQuery, selectedFilters, isSearchDataLoaded, _ ->
        if (!isUiReady) {
            return@combine SearchResultsViewState.Loading
        }
        if (!isVisible) {
            return@combine SearchResultsViewState.Inactive
        }
        if (!isSearchDataLoaded) {
            // To track the frequency of this scenario
            crashTracking.logNonFatalException(
                IllegalStateException("Search content is not fetched and search attempted")
            )
            // Search content is not fetched so make a hard exit
            appStateManager.navigateToError(ErrorScreen.RemoteDataFetch)
            hide()
            appStateManager.dismissSearchInput()
            return@combine SearchResultsViewState.Inactive
        }
        viewStateFactory.createSearchResultsViewState(
            searchResults,
            searchQuery,
            suggestedCollections,
            contentPrefetcher,
            scrollToTop,
            eventHandlerCloseSearch,
            feedScrollPositionUpdateSink,
            viewStateFactory.createSearchRecipeBinViewState(
                data = selectedFilters,
                screenInput = false,
                eventHandlerCloseSearch = eventHandlerCloseSearch,
            )
        )
    }.stateIn(initialValue = SearchResultsViewState.Loading)

}