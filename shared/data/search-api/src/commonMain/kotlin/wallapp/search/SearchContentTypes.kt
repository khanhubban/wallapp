package wallapp.search

data class SearchContentTypes(val types: List<SearchContentType>) {

    constructor(vararg types: SearchContentType) : this(types.toList())
}
