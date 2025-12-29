package wallapp.search

import wallapp.search.SearchArgumentMapper.mapSearchMatchStrategiesForQuery

data class SearchArguments(
    val query: String,
    val searchContentTypes: SearchContentTypes = SearchContentType.AllWallpapers,
    val dataTypes: List<SearchDataType> = SearchDataType.All,
    val searchMatchStrategies: List<SearchMatchStrategies> = mapSearchMatchStrategiesForQuery(query, searchContentTypes),
    val filterByQueryQuality: Boolean = true,
    /**
     * Can be [false] in the event we are searching just by content type, e.g. all collections.
     */
    val requireValidQuery: Boolean = true,
) {
    init {
        if (requireValidQuery) {
            require(query.isNotBlank()) { "Query must not be blank" }
            require(query.isNotEmpty()) { "Query must not be empty" }
        }
        require(dataTypes.isNotEmpty()) { "Data types must not be empty" }
        require(searchContentTypes.types.isNotEmpty()) { "Content types must not be empty" }
        searchContentTypes.types.validateGroupings()
    }

    val searchArtists: Boolean by lazy { searchContentTypes.types.contains(SearchContentType.Artists) }
    val searchFolders: Boolean by lazy { searchContentTypes.types.contains(SearchContentType.Folders) }
    val searchSingles: Boolean by lazy { searchContentTypes.types.contains(SearchContentType.Singles) }
    val searchTracks: Boolean by lazy { searchContentTypes.types.contains(SearchContentType.Tracks) }

    private fun List<SearchContentType>.validateGroupings() {
        if (contains(SearchContentType.Singles) || contains(SearchContentType.Tracks)) {
            require(!contains(SearchContentType.Artists)) { "Artists and Singles cannot be grouped together" }
            require(!contains(SearchContentType.Folders)) { "Folders and Singles cannot be grouped together" }
        }
        if (contains(SearchContentType.Artists)) {
            require(!contains(SearchContentType.Folders)) { "Artists and Folders cannot be grouped together" }
            require(!contains(SearchContentType.Singles)) { "Artists and Singles cannot be grouped together" }
            require(!contains(SearchContentType.Tracks)) { "Artists and Tracks cannot be grouped together" }
        }
        if (contains(SearchContentType.Folders)) {
            require(!contains(SearchContentType.Artists)) { "Artists and Folders cannot be grouped together" }
            require(!contains(SearchContentType.Singles)) { "Folders and Singles cannot be grouped together" }
            require(!contains(SearchContentType.Tracks)) { "Folders and Tracks cannot be grouped together" }
        }
    }

    companion object {

        fun from(
            query: String?,
            searchContentTypes: SearchContentTypes,
            filterByQueryQuality: Boolean,
            requireValidQuery: Boolean,
            dataTypes: List<SearchDataType> = SearchDataType.All,
        ): SearchArguments? {
            if (requireValidQuery) {
                if (query.isNullOrEmpty()) return null
                if (query.isBlank()) return null
            }

            return SearchArguments(
                query = query ?: "",
                dataTypes = dataTypes,
                searchContentTypes = searchContentTypes,
                filterByQueryQuality = filterByQueryQuality,
                requireValidQuery = requireValidQuery,
            )
        }
    }
}
