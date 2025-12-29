package wallapp.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.annotation.VisibleForTesting
import wallapp.content.model.ContentCategory
import wallapp.content.model.ContentCategorySpec
import wallapp.content.model.sorted
import wallapp.data.content.ContentCategoryFactory
import wallapp.log.Logger
import wallapp.search.SearchArgumentMapper.mapSearchArguments
import wallapp.search.SearchResultArbitrator.arbitrateSearchContentTypes
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchCategorySpec
import wallapp.search.model.SearchColor
import wallapp.util.combine

class SearchSessionManagerDefault(
    private val searchResultManager: SearchResultManager,
    private val searchCategorySpecFactory: SearchCategorySpecFactory,
    private val searchInputDefaults: SearchInputDefaults,
    private val contentCategoryFactory: ContentCategoryFactory,
    private val coroutineScopeMain: CoroutineScope,
) : SearchSessionManager {

    companion object {
        val Log = Logger("SearchSessionManager")
    }

    override val searchQuery: MutableStateFlow<String?> = MutableStateFlow(null)

    override fun updateSearchQuery(query: String?) {
        searchQuery.value = query
    }

    override val searchQueryFocused: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override fun setSearchQueryFocused(focused: Boolean) {
        searchQueryFocused.value = focused
    }

    private val selectedColors: MutableStateFlow<List<SearchColor>> =
        MutableStateFlow(emptyList())
    override val colors: Flow<List<Pair<SearchColor, Boolean>>> =
        selectedColors.map { selectedColors ->
            SearchColor.All.map { color ->
                color to (color in selectedColors)
            }
        }

    override fun setSelectedColor(color: SearchColor) {
        // only allow one color to be selected at a time
        selectedColors.value = if (color in selectedColors.value) {
            emptyList()
        } else {
            listOf(color)
        }

//        val selectedColors = selectedColors.value
//        (if (color in selectedColors) {
//            selectedColors - color
//        } else {
//            selectedColors + color
//        }).let {
//            this.selectedColors.value = it
//        }
    }

    private val selectedContentCategoriesDefault: List<ContentCategory>
        get() = searchInputDefaults.selectedContentCategoryDefault
    @VisibleForTesting
    val selectedContentCategories: MutableStateFlow<List<ContentCategory>> =
        MutableStateFlow(selectedContentCategoriesDefault)
    override val contentCategorySpecs: Flow<List<Pair<ContentCategorySpec, Boolean>>> =
        selectedContentCategories.map { contentCategories ->
            contentCategoryFactory.all.map { contentCategorySpec ->
                val contentCategory = contentCategorySpec.contentCategory
                contentCategorySpec to (contentCategory in contentCategories)
            }
        }
    override val contentCategories: Flow<List<Pair<ContentCategory, Boolean>>> =
        contentCategorySpecs.map { contentCategorySpecs ->
            contentCategorySpecs.map { it.first.contentCategory to it.second }
        }

    override fun toggleContentCategory(contentCategory: ContentCategory) {
        val selectedContentCategories = selectedContentCategories.value
        (when (contentCategory) {
            in selectedContentCategories -> {
                // If the category is already selected, remove it
                selectedContentCategories - contentCategory
            }
            ContentCategory.Singles -> {
                listOf(contentCategory)
            }
            ContentCategory.Collection -> {
                listOf(contentCategory)
            }
        }).let { selected ->
            this.selectedContentCategories.value = if (selected.isEmpty()) {
                emptyList()
            } else {
                selected.sorted()
            }
        }
    }

    @VisibleForTesting
    val selectedSearchContentTypes: Flow<SearchContentTypes> by lazy {
        combine(
            contentCategories,
            selectedColors,
            selectedCategories,
        ) { contentCategories, colors, categories ->
            arbitrateSearchContentTypes(contentCategories, colors, categories)
        }
    }
    private val commonSearchContentTypesLists: List<SearchContentTypes> = listOf(
        SearchContentTypes(SearchContentType.Folders),
        SearchContentTypes(SearchContentType.Artists),
    )

    private val searchContentTypesList: Flow<List<SearchContentTypes>> by lazy {
        selectedSearchContentTypes.map {
            listOf(SearchContentTypes(it.types)) + commonSearchContentTypesLists
        }
    }

    private val selectedCategorySpecs: MutableStateFlow<List<SearchCategorySpec>> =
        MutableStateFlow(emptyList())
    private val selectedCategories: Flow<List<SearchCategory>> =
        selectedCategorySpecs.map { selectedCategorySpecs ->
            selectedCategorySpecs.map { it.searchCategory }
        }
    override val categories: Flow<List<Pair<SearchCategorySpec, Boolean>>> =
        selectedCategorySpecs.map { selectedCategories ->
            searchCategorySpecFactory.all.map { category ->
                category to (category in selectedCategories)
            }
        }

    override fun setSelectedCategory(category: SearchCategorySpec) {
        // only allow one Category to be selected at a time
        selectedCategorySpecs.value = if (category in selectedCategorySpecs.value) {
            emptyList()
        } else {
            listOf(category)
        }
    }

//    override fun toggleCategory(category: SearchCategorySpec) {
//        val selectedCategories = selectedCategories.value
//        (if (category in selectedCategories) {
//            selectedCategories - category
//        } else {
//            selectedCategories + category
//        }).let {
//            this.selectedCategories.value = it
//        }
//    }

    override val hasAnyFilterInput: Flow<Boolean> = combine(
        selectedColors,
        selectedCategorySpecs,
        selectedContentCategories,
    ) { colors, categories, contentCategories ->
        colors.isNotEmpty() ||
        categories.isNotEmpty() ||
        contentCategories.isNotEmpty()
    }

    override val hasAnyUserInput: Flow<Boolean> = combine(
        searchQuery,
        hasAnyFilterInput,
    ) { searchQuery, hasAnyFilterInput ->
        !searchQuery.isNullOrEmpty() || hasAnyFilterInput
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val searchResults: Flow<SearchResult> =
        /**
         * [searchContentTypes] is not included in the [combine] call below because
         * [selectedSearchContentTypes], which it depends on, must be updated via
         * [arbitrateSearchContentTypes] before the other filters are applied. This ensures
         * [selectedCategories] will be set correctly - it's a bit unique because it can change
         * between [SearchContentType.Collections] and [SearchContentType.Tracks] based on the
         * presence other filters.
         *
         * Without this [flatMapLatest] call, it's possible for [selectedColors] to be updated
         * before [selectedSearchContentTypes] has been updated via [arbitrateSearchContentTypes],
         * which can lead to an invalid state.
         */
        searchContentTypesList.flatMapLatest { searchContentTypesList: List<SearchContentTypes> ->
            combine(
                searchQuery,
                selectedCategories,
                selectedColors,
                hasAnyUserInput,
            ) { query, categories, colors, hasAnyUserInput ->
                if (hasAnyUserInput) {
                    val searchArguments = mapSearchArguments(
                        query = query,
                        searchContentTypesLists = searchContentTypesList,
                        searchColors = colors.ifEmpty { null },
                        searchCategories = categories.ifEmpty { null },
                    )
                    searchResultManager.get(searchArguments)
                } else {
                    flowOf(SearchResult.Inactive)
                }
            }
        }.flatMapLatest { it }

    /**
     * Each [SearchContentTypes] list will be mapped to its own [SearchArguments].
     *
     * This allows finer control such as setting [SearchArguments.searchMatchStrategies] for each
     * [SearchContentTypes] list.
     */
    private fun mapSearchArguments(
        query: String?,
        searchContentTypesLists: List<SearchContentTypes>,
        searchColors: List<SearchColor>?,
        searchCategories: List<SearchCategory>?,
    ): List<SearchArguments> {
        return searchContentTypesLists.map { searchContentTypes: SearchContentTypes ->
            mapSearchArguments(
                query = query,
                searchContentTypes = searchContentTypes,
                searchColors = searchColors,
                searchCategories = searchCategories,
            )
        }.flatten()
    }

    override val selectedFilters: StateFlow<List<Any>> = combine(
        colors.map { colors -> colors.filter { it.second }.map { it.first } },
        contentCategorySpecs.map { contentCategories -> contentCategories.filter { it.second }.map { it.first } },
        categories.map { categories -> categories.filter { it.second }.map { it.first } },
    ) { colors, contentCategories, categories ->
        listOf(colors, contentCategories, categories).flatten()
    }.stateIn(scope = coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = emptyList())

    override fun resetAll() {
        resetSearchQuery()
        selectedColors.value = emptyList()
        selectedContentCategories.value = selectedContentCategoriesDefault
        selectedCategorySpecs.value = emptyList()
    }

    override fun resetSearchQuery() {
        updateSearchQuery(null)
    }

    override fun resetSearchFilters() {
        selectedColors.value = emptyList()
        selectedContentCategories.value = emptyList()
        selectedCategorySpecs.value = emptyList()
    }
}