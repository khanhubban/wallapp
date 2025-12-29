package wallapp.search.sort

import wallapp.search.SearchQueryResult

interface SearchResultSorter {

    fun sort(result: SearchQueryResult): SearchQueryResult
}