package wallapp.search

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop

@SealedInterop.Enabled
sealed class SearchQueryResult {

    @Immutable
    data object Inactive: SearchQueryResult()

    @Immutable
    data object Loading : SearchQueryResult()

    @Immutable
    data object NoResults : SearchQueryResult()

    @Immutable
    data class Results(val itemResults: List<SearchQueryItemResult>) : SearchQueryResult()

    companion object {

        fun SearchQueryResult.combineSearchQueryResultUnion(
            other: SearchQueryResult,
        ): SearchQueryResult {
            val thisResults: Results? = this as? Results
            val otherResults: Results? = other as? Results

            return if (thisResults != null && otherResults == null) {
                thisResults
            } else if (thisResults == null && otherResults != null) {
                otherResults
            } else if (thisResults != null && otherResults != null) {
                Results(thisResults.itemResults + otherResults.itemResults)
            } else {
                if (this is NoResults || other is NoResults) {
                    NoResults
                } else if (this is Loading || other is Loading) {
                    Loading
                } else if (this is Inactive || other is Inactive) {
                    Inactive
                } else {
                    // Ideally this never happens
                    this
                }
            }
        }

        fun SearchQueryResult.combineSearchQueryResultUnion(vararg results: SearchQueryResult): SearchQueryResult {
            val allResults = listOf(this) + results

            return allResults
                .reduce { acc, result ->
                    acc.combineSearchQueryResultUnion(result)
                }.distinct()
        }

        fun SearchQueryResult.combineSearchQueryResultIntersect(
            other: SearchQueryResult,
        ): SearchQueryResult {
            val thisResults: Results? = this as? Results
            val otherResults: Results? = other as? Results

            return if (thisResults != null && otherResults != null) {
                val thisItemsById = thisResults.itemResults.associateBy { it.id }
                val otherItemsById = otherResults.itemResults.associateBy { it.id }
                val commonItems = thisItemsById.keys.intersect(otherItemsById.keys).map { id ->
                    thisItemsById[id] ?: otherItemsById[id]
                }.filterNotNull()
                    .ifEmpty { null }
                if (commonItems != null) {
                    Results(commonItems)
                } else {
                    NoResults
                }
            } else if (thisResults != null && otherResults == null) {
                thisResults
            } else if (thisResults == null && otherResults != null) {
                otherResults
            } else {
                if (this is NoResults || other is NoResults) {
                    NoResults
                } else if (this is Loading || other is Loading) {
                    Loading
                } else if (this is Inactive || other is Inactive) {
                    Inactive
                } else {
                    // Ideally this never happens
                    this
                }
            }
        }

        fun SearchQueryResult.combineSearchQueryResultIntersect(vararg results: SearchQueryResult): SearchQueryResult {
            val allResults = listOf(this) + results

            return allResults.reduce { acc, result ->
                acc.combineSearchQueryResultIntersect(result)
            }
        }

        fun SearchQueryResult.distinct(): SearchQueryResult {
            return when (this) {
                is Results -> Results(itemResults.distinctBy { it.id })
                else -> this
            }
        }

        val SearchQueryResult.debugString: String
            get() = when (this) {
                Inactive -> "Inactive"
                Loading -> "Loading"
                NoResults -> "NoResults"
                is Results -> {
                    "Results(size:${itemResults.size})\n" + itemResults.joinToString("\n") { "  ${it.debugString}" }
                }
            }

    }
}