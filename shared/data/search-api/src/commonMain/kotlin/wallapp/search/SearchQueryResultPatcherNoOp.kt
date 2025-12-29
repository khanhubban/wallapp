package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object SearchQueryResultPatcherNoOp : SearchQueryResultPatcher {

    override fun patch(searchQueryResult: SearchQueryResult): Flow<SearchQueryResult> = flowOf(searchQueryResult)
}