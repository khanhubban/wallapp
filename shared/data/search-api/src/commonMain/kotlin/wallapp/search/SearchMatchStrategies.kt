package wallapp.search

data class SearchMatchStrategies(val strategies: List<SearchMatchStrategy>) {

    constructor(strategy: SearchMatchStrategy) : this(listOf(strategy))
}