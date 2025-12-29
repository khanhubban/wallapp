package wallapp.search.sort

import wallapp.random.RandomManager
import wallapp.search.SearchQueryItemResult
import wallapp.search.SearchQueryResult
import wallapp.search.curators
import kotlin.random.Random

class SearchResultSorterDefault(
    private val randomManager: RandomManager,
) : SearchResultSorter {

    private val deterministicRandom: Random
        get() = randomManager.deterministicRandom

    private fun SearchQueryResult.Results.sortedByWeight(): SearchQueryResult.Results {
        val itemResultsCurators = itemResults.curators ?: emptyList()
        val itemResultsSansCurators = itemResults.filterNot { it is SearchQueryItemResult.ResultCurator }

        // Always put curators first, sorted by weight and then alphabetically
        return SearchQueryResult.Results(
            itemResults = itemResultsCurators.sortedByWeightAndAlphabetical() +
                    itemResultsSansCurators.sortedByWeightAndRandom()
        )
    }

    private fun List<SearchQueryItemResult.ResultCurator>.sortedByWeightAndAlphabetical(): List<SearchQueryItemResult> {
        val groupedByWeight: Map<Float, List<SearchQueryItemResult.ResultCurator>> =
            groupBy { it.searchFilterMatch.weight }
        val sortedWeights = groupedByWeight.keys.sortedDescending()

        val sortedByWeight = sortedWeights.flatMap { weight ->
            groupedByWeight[weight]?.sortedBy { it.curator.title } ?: emptyList()
        }

        return sortedByWeight
    }

    private fun List<SearchQueryItemResult>.sortedByWeightAndRandom(): List<SearchQueryItemResult> {
        val groupedByWeight: Map<Float, List<SearchQueryItemResult>> =
            groupBy { it.searchFilterMatch.weight }
        val sortedWeights = groupedByWeight.keys.sortedDescending()

        val sortedByWeight = sortedWeights.flatMap { weight ->
            groupedByWeight[weight]?.shuffled(deterministicRandom) ?: emptyList()
        }

        return sortedByWeight
    }

    private fun SearchQueryResult.sortedByWeight(): SearchQueryResult {
        return when (this) {
            is SearchQueryResult.Results -> sortedByWeight()
            else -> this
        }
    }

    override fun sort(result: SearchQueryResult): SearchQueryResult {
        return result.sortedByWeight()
    }
}