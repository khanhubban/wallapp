package wallapp.content.state.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import wallapp.app.AppStateManager
import wallapp.content.model.ContentCategorySpec
import wallapp.graphics.lerp
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.selection.SelectionViewEventSink
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.search.SearchSessionManager
import wallapp.search.model.SearchCategorySpec
import wallapp.search.model.SearchColor
import wallapp.theme.ColorToken
import wallapp.theme.ThemeManager
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class SearchInputViewModel(
    private val searchSessionManager: SearchSessionManager,
    private val viewStateFactory: ViewStateFactory,
    viewEventFactory: ViewEventFactory,
    private val appStateManager: AppStateManager,
    viewStateRefresher: ViewStateRefresher,
    private val themeManager: ThemeManager,
) : ViewModel(), ScreenViewStateProvider {

    private var resetSearchOnDismiss: Boolean = false

    // This is needed to ensure that transition is disabled when the search bar is dismissed
    private val searchBarTransitionDisabled = MutableStateFlow(true)

    private val eventSink: SearchViewEventSink = { event ->
        when (event) {
            is SearchViewEvent.Clear -> {
                searchSessionManager.resetSearchQuery()
            }

            is SearchViewEvent.CloseSearch -> {
                searchBarTransitionDisabled.value = true
                resetSearchOnDismiss = true
                appStateManager.dismissSearch()
            }

            is SearchViewEvent.QueryChange -> {
                searchSessionManager.updateSearchQuery(event.query)
                searchBarTransitionDisabled.value = false
            }

            is SearchViewEvent.QuerySubmit -> {
                appStateManager.dismissSearchInput()
                searchBarTransitionDisabled.value = false
            }

            is SearchViewEvent.QueryFocused -> {
                val focused = event.focused
                searchSessionManager.setSearchQueryFocused(focused)
                searchSessionManager.resetSearchFilters()
            }

            is SearchViewEvent.SearchColorToggle -> {
                searchSessionManager.setSelectedColor(event.color)
                searchBarTransitionDisabled.value = false
            }
        }
    }

    private val selectionViewEventSink: SelectionViewEventSink = { event ->
        searchBarTransitionDisabled.value = false
        val key = event.key

        when (key) {
            is ContentCategorySpec -> {
                searchSessionManager.toggleContentCategory(key.contentCategory)
            }

            is SearchCategorySpec -> {
                searchSessionManager.setSelectedCategory(key)
            }
        }
    }

    private val onSearchScrimClick: ViewEventHandler = viewEventFactory.createNavigateBack()

    @Suppress("UNCHECKED_CAST")
    private val eventHandlerCloseSearch = ViewEventHandler.Event(
        eventSink = eventSink as ViewEventSink,
        event = SearchViewEvent.CloseSearch
    )

    @Suppress("UNCHECKED_CAST")
    private val eventHandlerQuerySubmit = ViewEventHandler.Event(
        eventSink = eventSink as ViewEventSink,
        event = SearchViewEvent.QuerySubmit(null)
    )

    val overlayProgress: MutableStateFlow<Float> = MutableStateFlow(0f)

    fun onOverlayVisibilityProgress(progress: Float) {
        overlayProgress.value = progress
    }

    private fun createViewState(
        isUiReady: Boolean,
        query: String? = null,
        searchQueryFocused: Boolean = false,
        colors: List<Pair<SearchColor, Boolean>> = emptyList(),
        contentCategorySpecs: List<Pair<ContentCategorySpec, Boolean>> = emptyList(),
        categories: List<Pair<SearchCategorySpec, Boolean>> = emptyList(),
        selectedFilters: List<Any> = emptyList(),
    ): SearchInputViewState {
        if (!isUiReady) {
            return SearchInputViewState.Loading
        }

        val searchFieldFocused = !query.isNullOrEmpty() || searchQueryFocused

        val visibleProgress = overlayProgress.value
        val defaultContainerColor = themeManager.theme.value.themeColors.background
        val dismissedContainerColor = themeManager.theme.value.themeColors.surface
        val searchBarContainerColor = ColorToken.Custom(
            lerp(
                dismissedContainerColor,
                defaultContainerColor,
                visibleProgress
            )
        )
        val searchBarIconContainerColor = ColorToken.Custom(
            lerp(
                defaultContainerColor,
                dismissedContainerColor,
                visibleProgress
            )
        )

        return viewStateFactory.createSearchInputViewState(
            eventSink = eventSink,
            searchSessionManager = searchSessionManager,
            searchFieldFocused = searchFieldFocused,
            searchColors = viewStateFactory.createSearchColorsViewState(eventSink, colors),
            contentCategoryGroup = viewStateFactory.createSearchContentCategorySelectionGroup(selectionViewEventSink, contentCategorySpecs),
            searchFilterGroup = viewStateFactory.createSearchCategoriesSelectionGroup(selectionViewEventSink, categories),
            searchRecipeBinState = viewStateFactory.createSearchRecipeBinViewState(
                data = selectedFilters,
                screenInput = true,
                eventHandlerCloseSearch = eventHandlerCloseSearch,
                eventHandlerQuerySubmit = eventHandlerQuerySubmit,
                containerColor = searchBarContainerColor,
                iconContainerColor = searchBarIconContainerColor,
                visibleProgress = visibleProgress,
            ),
            onScrimClick = onSearchScrimClick,
            searchBarTransitionDisabled = searchBarTransitionDisabled.value,
            searchBarContainerColor = searchBarContainerColor,
            searchBarIconContainerColor = searchBarIconContainerColor,
        )
    }

    override val viewState: StateFlow<SearchInputViewState> =
        combine(
            appStateManager.isUiReady,
            searchSessionManager.searchQuery,
            searchSessionManager.searchQueryFocused,
            searchSessionManager.colors,
            searchSessionManager.contentCategorySpecs,
            searchSessionManager.categories,
            searchSessionManager.selectedFilters,
            searchBarTransitionDisabled.filter { it },
            viewStateRefresher.refresh,
            overlayProgress,
        ) { isUiReady, query, searchQueryFocused, colors, contentTypeSpecs, categories, selectedFilters, _, _, _ ->
            createViewState(isUiReady, query, searchQueryFocused, colors, contentTypeSpecs, categories, selectedFilters)
        }.stateIn(createViewState(isUiReady = false))

    fun onSearchInputOverlayDismissed() {
        if (resetSearchOnDismiss) {
            searchSessionManager.resetAll()
        }
        resetSearchOnDismiss = false
    }

}