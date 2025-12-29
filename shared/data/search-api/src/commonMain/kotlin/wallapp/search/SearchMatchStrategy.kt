package wallapp.search

sealed class SearchMatchStrategy {

    data object ExactMatch : SearchMatchStrategy()

    // The start matches with any of the words in the term. So "for" would match for "dark forest".
    data object PartialMatchStartAny : SearchMatchStrategy()

    data object PartialMatchContains : SearchMatchStrategy()

    data class FuzzyMatch(val thresholdAllowed: Int) : SearchMatchStrategy()
}