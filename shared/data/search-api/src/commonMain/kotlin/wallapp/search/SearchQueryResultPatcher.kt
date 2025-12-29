package wallapp.search

import kotlinx.coroutines.flow.Flow

/**
 * Patches a [SearchQueryResult] with additional data. For example, if a Folder is included in the
 * search results, append all the wallpapers in that folder to the search results.
 */
interface SearchQueryResultPatcher {

    fun patch(searchQueryResult: SearchQueryResult): Flow<SearchQueryResult>
}