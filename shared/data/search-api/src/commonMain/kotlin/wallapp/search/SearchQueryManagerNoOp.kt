package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object SearchQueryManagerNoOp : SearchQueryManager {
    
    override fun search(searchArguments: List<SearchArguments>): Flow<SearchQueryResult> =
        flowOf(SearchQueryResult.Inactive)
}