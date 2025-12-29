package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import wallapp.di.resolveDependency
import wallapp.search.content.SearchContentRepositoryDefault

suspend fun SearchContentRepositoryDefault(
    waitUntilReady: Boolean,
): SearchContentRepositoryDefault {
    val manager = resolveDependency<SearchContentRepositoryDefault>()

    if (waitUntilReady) {
        manager.isReady.filter { it }.first()
    }

    return manager
}

suspend fun SearchQueryManagerDefault(
    waitUntilReady: Boolean = true,
): SearchQueryManagerDefault {

    val manager = resolveDependency<SearchQueryManagerDefault>()
    if (waitUntilReady) {
        manager.isReady.filter { it }.first()
    }

    return manager
}

suspend fun Flow<SearchQueryResult>.waitForFirstNonLoadingResult(): SearchQueryResult {
    return first { it !is SearchQueryResult.Loading }
}

