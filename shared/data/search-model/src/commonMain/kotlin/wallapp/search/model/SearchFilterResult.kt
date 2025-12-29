package wallapp.search.model

sealed class SearchFilterResult {

    data object InvalidQuery : SearchFilterResult()

    data object NoResults : SearchFilterResult()

    data class Results(
        val matches: List<SearchFilterMatch>,
    ) : SearchFilterResult()
}