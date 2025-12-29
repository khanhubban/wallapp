package wallapp.search

import kotlinx.coroutines.flow.Flow

interface SearchQueryManager {

    fun search(searchArguments: List<SearchArguments>): Flow<SearchQueryResult>
}