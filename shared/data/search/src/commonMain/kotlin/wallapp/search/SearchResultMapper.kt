package wallapp.search

import wallapp.content.model.Wallpaper
import wallapp.content.model.wallpapers
import wallapp.data.collection.CollectionState
import wallapp.data.curator.Curator
import wallapp.log.Logger
import wallapp.search.SearchQueryItemResult.ResultCollection
import wallapp.search.SearchQueryItemResult.ResultCurator

object SearchResultMapper {

    val Log = Logger("SearchResultsArbitrator")

    val SearchQueryResult.curators: List<Curator>?
        get() = when (this) {
            is SearchQueryResult.Results -> this.itemResults
                .filterIsInstance<ResultCurator>()
                .map { it.curator }
            is SearchQueryResult.NoResults -> emptyList()
            else -> null
        }

    val SearchQueryResult.wallpapers: List<Wallpaper>?
        get() = when (this) {
            is SearchQueryResult.Results -> this.itemResults
                .mapNotNull { it.wallpaper }
                .wallpapers
            is SearchQueryResult.NoResults -> emptyList()
            else -> null
        }

    val SearchQueryResult.collectionResults: List<ResultCollection>?
        get() = when (this) {
            is SearchQueryResult.Results -> this.itemResults
                .filterIsInstance<ResultCollection>()
            is SearchQueryResult.NoResults -> emptyList()
            else -> null
        }

    fun SearchQueryResult.mapWallpapers(): List<Wallpaper>? {
        return wallpapers.also {
            Log.d("arbitrateWallpapers() queryWallpapers=${it?.size}")
        }
    }

    fun CollectionState.mapCollectionPreviewWallpaper(
        wallpaper: Wallpaper?,
    ): CollectionState {
        return if (wallpaper != null) {
            this.copy(
                wallpapers = (listOf(wallpaper) + wallpapers).distinct()
            )
        } else {
            this
        }
    }

    fun SearchQueryResult.mapCollections(allCollectionStates: List<CollectionState>): List<CollectionState>? {
        return collectionResults
            ?.map { resultCollection ->
                val collectionState = allCollectionStates.find {
                    it.id == resultCollection.category.id.collectionId
                }
                requireNotNull(collectionState) { "CollectionState not found for ${resultCollection.category.id.collectionId}" }

                collectionState.mapCollectionPreviewWallpaper(resultCollection.wallpaper)
            }
            .also {
                Log.d("arbitrateCollections() queryWallpapers=${it?.size}")
            }
    }

    fun mapSearchResults(
        searchQueryResult: SearchQueryResult,
        allCollectionStates: List<CollectionState>,
    ): SearchResult {
        when (searchQueryResult) {
            is SearchQueryResult.Inactive -> return SearchResult.Inactive
            is SearchQueryResult.Loading -> return SearchResult.Loading
            is SearchQueryResult.NoResults, is SearchQueryResult.Results -> {
                val wallpapers = searchQueryResult.mapWallpapers()
                val collections = searchQueryResult.mapCollections(allCollectionStates)
                val curators = searchQueryResult.curators

                if (wallpapers == null && collections == null && curators == null) {
                    return SearchResult.NoResults
                }

                return SearchResult.Results(
                    wallpapers = wallpapers,
                    collectionStates = collections,
                    curators = curators,
                )
            }
        }
    }
}