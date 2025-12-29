package wallapp.search

sealed class SearchContentType {

    data object Artists : SearchContentType()
    data object Folders : SearchContentType()
    data object Singles : SearchContentType()
    data object Tracks : SearchContentType()

    companion object {
        val AllWallpapers: SearchContentTypes by lazy {
            SearchContentTypes(listOf(Tracks, Singles))
        }
    }
}

