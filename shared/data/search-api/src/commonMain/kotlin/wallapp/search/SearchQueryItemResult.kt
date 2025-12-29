package wallapp.search

import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.data.artist.Artist
import wallapp.data.curator.Curator
import wallapp.data.folder.Folder
import wallapp.search.model.SearchFilterMatch
import wallapp.string.quote

sealed class SearchQueryItemResult {

    abstract val searchFilterMatch: SearchFilterMatch
    abstract val searchMetadata: Any? // used for debugging
    abstract val id: Id

    private val debugId: String
        get() = when (this) {
            is ResultCollection -> "${category.id.name.quote()}}/${wallpaper.id.name.quote()}"
            is ResultCurator -> curator.id.name.quote()
            is ResultWallpaper -> wallpaper.id.name.quote()
        }

    data class ResultCollection(
        val category: WallpaperCategory,
        /**
         * The wallpaper to display as the preview for the collection. If null, the Collection
         * default will be displayed.
         */
        val wallpaper: Wallpaper,
        override val searchFilterMatch: SearchFilterMatch,
        override val searchMetadata: Any? = null,
    ) : SearchQueryItemResult() {
        override val id: Id
            get() = category.id
    }

    data class ResultCurator(
        val curator: Curator,
        override val searchFilterMatch: SearchFilterMatch,
        override val searchMetadata: Any? = null,
    ) : SearchQueryItemResult() {
        override val id: Id
            get() = curator.id
    }

    data class ResultWallpaper(
        val wallpaper: Wallpaper,
        override val searchFilterMatch: SearchFilterMatch,
        override val searchMetadata: Any? = null,
    ) : SearchQueryItemResult() {
        override val id: Id
            get() = wallpaper.id
    }

    val debugString: String
        get() = "[SearchQueryItemResult]\n  wallpaperItem: ${debugId}\n" +
                "  searchFilterResult: $searchFilterMatch"
//                "  searchRemixMetadata: ${searchRemixMetadata.debugString}\n"
}

val SearchQueryItemResult.wallpaper: Wallpaper?
    get() = when (this) {
        is SearchQueryItemResult.ResultCollection -> wallpaper
        is SearchQueryItemResult.ResultCurator -> null
        is SearchQueryItemResult.ResultWallpaper -> wallpaper
    }

val List<SearchQueryItemResult>.curators: List<SearchQueryItemResult.ResultCurator>?
    get() = filterIsInstance<SearchQueryItemResult.ResultCurator>().ifEmpty { null }
val List<SearchQueryItemResult>.folders: List<SearchQueryItemResult.ResultCurator>?
    get() = curators
        ?.filter { it.curator is Folder }
        ?.ifEmpty { null }
val List<SearchQueryItemResult>.artists: List<SearchQueryItemResult.ResultCurator>?
    get() = curators
        ?.filter { it.curator is Artist }
        ?.ifEmpty { null }
val List<SearchQueryItemResult>.wallpapers: List<SearchQueryItemResult.ResultWallpaper>?
    get() = filterIsInstance<SearchQueryItemResult.ResultWallpaper>().ifEmpty { null }
val List<SearchQueryItemResult>.singles: List<SearchQueryItemResult.ResultWallpaper>?
    get() = filterIsInstance<SearchQueryItemResult.ResultWallpaper>()
        .filter { it.wallpaper.isSingle }
        .ifEmpty { null }
val List<SearchQueryItemResult>.tracks: List<SearchQueryItemResult.ResultWallpaper>?
    get() = filterIsInstance<SearchQueryItemResult.ResultWallpaper>()
        .filter { it.wallpaper.isTrack }
        .ifEmpty { null }
