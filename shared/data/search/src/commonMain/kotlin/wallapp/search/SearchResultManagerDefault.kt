package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import wallapp.data.content.ContentRepository
import wallapp.log.Logger
import wallapp.search.SearchArgumentMapper.mapSearchArguments
import wallapp.search.SearchQueryResult.Companion.combineSearchQueryResultUnion
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchColor
import wallapp.search.sort.SearchResultSorter

class SearchResultManagerDefault(
    private val searchQueryManager: SearchQueryManager,
    private val contentRepository: ContentRepository,
    private val searchResultSorter: SearchResultSorter,
) : SearchResultManager {

    companion object {
        val Log = Logger("SearchResultManager")
    }

    /**
     * Split this function out so it can be tested separately. This way, a
     * [SearchResultManagerDefault] instance can be tested without requiring a [contentRepository]
     * instance.
     */
    fun getSearchQueryResult(
        query: String?,
        searchContentTypes: SearchContentTypes,
        searchColors: List<SearchColor>?,
        searchCategories: List<SearchCategory>?,
    ): Flow<SearchQueryResult> {
        Log.d("getSearchQueryResult(): query: $query, searchContentTypes: $searchContentTypes, searchColors: $searchColors, searchCategories: $searchCategories")
        val searchArguments = mapSearchArguments(
            query,
            searchContentTypes,
            searchColors,
            searchCategories,
        )
        return getSearchQueryResult(searchArguments)
    }

    fun getSearchQueryResult(searchArguments: List<SearchArguments>): Flow<SearchQueryResult> {
        val searchResults: List<Flow<SearchQueryResult>> = searchArguments
            .map { searchQueryManager.search(listOf(it)) }

        // Combine all Flow<SearchQueryResult> into a single Flow<SearchQueryResult>
        return combine(searchResults) { results: Array<SearchQueryResult> ->
            results.reduce { acc, result ->
                acc.combineSearchQueryResultUnion(result)
            }
        }.map { combinedResult ->
            searchResultSorter.sort(combinedResult)
        }
    }

    override fun get(
        searchArguments: List<SearchArguments>,
    ): Flow<SearchResult> {
        return combine(
            getSearchQueryResult(searchArguments),
            contentRepository.collectionStates,
        ) { searchQueryResult, allCollectionStates, ->
            SearchResultMapper.mapSearchResults(
                searchQueryResult = searchQueryResult,
                allCollectionStates = allCollectionStates,
            )
        }
    }
}


suspend fun Flow<SearchResult>.waitForFirstNonLoadingResult(): SearchResult {
    return first { it !is SearchResult.Loading }
}
