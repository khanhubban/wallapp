package wallapp.search

sealed class SearchDataType {
    data object Artist : SearchDataType()
    data object Title : SearchDataType()
    data object CollectionTitle : SearchDataType()
    data object Colors : SearchDataType()
    data object Categories : SearchDataType()
    data object Tags : SearchDataType()
    data object SearchTerms : SearchDataType()

    companion object {
        val All: List<SearchDataType> by lazy {
            listOf(Artist, Title, CollectionTitle, Colors, Categories, Tags, SearchTerms)
        }
    }
}

val List<SearchDataType>.containsAll: Boolean
    get() = this.containsAll(SearchDataType.All)
val List<SearchDataType>.containsArtist: Boolean
    get() = this.contains(SearchDataType.Artist)
val List<SearchDataType>.containsTitle: Boolean
    get() = this.contains(SearchDataType.Title)
val List<SearchDataType>.containsCollectionTitle: Boolean
    get() = this.contains(SearchDataType.CollectionTitle)
val List<SearchDataType>.containsColors: Boolean
    get() = this.contains(SearchDataType.Colors)
val List<SearchDataType>.containsCategories: Boolean
    get() = this.contains(SearchDataType.Categories)
val List<SearchDataType>.containsTags: Boolean
    get() = this.contains(SearchDataType.Tags)
val List<SearchDataType>.containsSearchTerms: Boolean
    get() = this.contains(SearchDataType.SearchTerms)
