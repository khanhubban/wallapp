package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

object SearchResultManagerNoOp : SearchResultManager {

    override fun get(searchArguments: List<SearchArguments>): Flow<SearchResult> = emptyFlow()
}