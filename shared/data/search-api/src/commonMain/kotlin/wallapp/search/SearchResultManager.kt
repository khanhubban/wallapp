package wallapp.search

import kotlinx.coroutines.flow.Flow

interface SearchResultManager {

    fun get(searchArguments: List<SearchArguments>): Flow<SearchResult>
}