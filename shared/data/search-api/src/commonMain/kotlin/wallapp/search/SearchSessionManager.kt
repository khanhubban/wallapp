package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.ContentCategory
import wallapp.content.model.ContentCategorySpec
import wallapp.search.model.SearchCategorySpec
import wallapp.search.model.SearchColor

interface SearchSessionManager {

    /**
     * true if search field is not empty, and/or any filter has been selected.
     */
    val hasAnyUserInput: Flow<Boolean>
    val hasAnyFilterInput: Flow<Boolean>

    val searchResults: Flow<SearchResult>

    val searchQuery: StateFlow<String?>
    fun updateSearchQuery(query: String?)

    val searchQueryFocused: StateFlow<Boolean>
    fun setSearchQueryFocused(focused: Boolean)

    val colors: Flow<List<Pair<SearchColor, Boolean>>>
    fun setSelectedColor(color: SearchColor)

    val contentCategories: Flow<List<Pair<ContentCategory, Boolean>>>
    val contentCategorySpecs: Flow<List<Pair<ContentCategorySpec, Boolean>>>
    fun toggleContentCategory(contentCategory: ContentCategory)

    val categories: Flow<List<Pair<SearchCategorySpec, Boolean>>>
    fun setSelectedCategory(category: SearchCategorySpec)

    val selectedFilters: Flow<List<Any>>

    fun resetAll()
    fun resetSearchQuery()
    fun resetSearchFilters()
}