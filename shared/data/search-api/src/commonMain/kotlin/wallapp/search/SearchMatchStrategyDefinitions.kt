package wallapp.search

import wallapp.search.SearchMatchStrategy.ExactMatch
import wallapp.search.SearchMatchStrategy.FuzzyMatch
import wallapp.search.SearchMatchStrategy.PartialMatchContains
import wallapp.search.SearchMatchStrategy.PartialMatchStartAny

object SearchMatchStrategyDefinitions {

    val ExactAndPartialStartAny = SearchMatchStrategies(
        listOf(
            ExactMatch,
            PartialMatchStartAny,
        )
    )

    val PartialContains = SearchMatchStrategies(
        listOf(
            PartialMatchContains,
        )
    )

    val Fuzzy1Step = SearchMatchStrategies(
        listOf(
            FuzzyMatch(thresholdAllowed = 1),
        )
    )

//    @Deprecated("2 step fuzzy match is not recommended as it produces too many innacurate results. #2075.")
//    val Fuzzy2Step = SearchMatchStrategies(
//        listOf(
//            FuzzyMatch(thresholdAllowed = 2),
//        )
//    )
}