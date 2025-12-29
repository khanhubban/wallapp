package wallapp.search

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import wallapp.annotation.VisibleForTesting
import wallapp.content.model.WallpaperItem
import wallapp.content.model.singles
import wallapp.content.model.tracks
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistRepository
import wallapp.data.folder.Folder
import wallapp.data.folder.FolderRepository
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.log.Logger
import wallapp.search.SearchQueryResult.Companion.combineSearchQueryResultUnion
import wallapp.search.SearchQueryResult.Companion.distinct
import wallapp.search.content.SearchContentRepository
import wallapp.search.filter.SearchFilterArbitrator.filterArtists
import wallapp.search.filter.SearchFilterArbitrator.filterFolders
import wallapp.search.filter.SearchFilterArbitrator.filterWallpapers
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFolderMetadata
import wallapp.search.model.SearchRemixMetadata
import wallapp.util.combine
import kotlin.jvm.JvmName

private data class SearchQueryData(
    val allWallpaperItems: List<WallpaperItem>,
    val allSearchRemixMetadata: List<SearchRemixMetadata>,
    val allArtists: List<Artist>,
    val allSearchArtistMetadata: List<SearchArtistMetadata>,
    val allFolders: List<Folder>,
    val allSearchFolderMetadata: List<SearchFolderMetadata>,
) {
    val isReady: Boolean
        get() = allSearchRemixMetadata.isNotEmpty() && allWallpaperItems.isNotEmpty()
                && allArtists.isNotEmpty() && allSearchArtistMetadata.isNotEmpty()
                && allFolders.isNotEmpty() && allSearchFolderMetadata.isNotEmpty()
}

class SearchQueryManagerDefault(
    searchContentRepository: SearchContentRepository,
    private val searchQueryResultPatcher: SearchQueryResultPatcher,
    wallpaperRepository: WallpaperRepository,
    private val artistRepository: ArtistRepository,
    private val folderRepository: FolderRepository,
) : SearchQueryManager {

    companion object {
        val Log = Logger("SearchQueryManager")
    }

    private val allWallpaperItems: Flow<List<WallpaperItem>> =
        wallpaperRepository.allWallpaperItems
    private val allSearchRemixMetadata: Flow<List<SearchRemixMetadata>> =
        searchContentRepository.allSearchRemixMetadata
    private val allArtists: Flow<List<Artist>>
        get() = artistRepository.artists
    private val allSearchArtistMetadata: Flow<List<SearchArtistMetadata>> =
        searchContentRepository.allSearchArtistMetadata
    private val allFolders: Flow<List<Folder>>
        get() = folderRepository.folders
    private val allSearchFolderMetadata: Flow<List<SearchFolderMetadata>> =
        searchContentRepository.allSearchFolderMetadata

    private val data: Flow<SearchQueryData?> =
        combine(
            allSearchRemixMetadata,
            allWallpaperItems,
            allSearchArtistMetadata,
            allArtists,
            allSearchFolderMetadata,
            allFolders,
        ) { remixMetadata, wallpaperItems, artistMetadata, artists, folderMetadata, folders ->

            SearchQueryData(
                allWallpaperItems = wallpaperItems,
                allSearchRemixMetadata = remixMetadata,
                allArtists = artists,
                allSearchArtistMetadata = artistMetadata,
                allFolders = folders,
                allSearchFolderMetadata = folderMetadata,
            ).let {
                if (it.isReady) it else null
            }
        }

    @VisibleForTesting
    val isReady: Flow<Boolean> = combine(
        allSearchRemixMetadata,
        allWallpaperItems,
    ) { wallpaperMetadata, items ->
        wallpaperMetadata.isNotEmpty() && items.isNotEmpty()
    }

    @JvmName("searchRemix")
    private suspend fun search(
        searchArguments: SearchArguments,
        allSearchRemixMetadata: List<SearchRemixMetadata>,
        wallpapers: List<WallpaperItem>,
    ): SearchQueryResult {

        /**
         * Iterate through each search match strategy until a result is found.
         *
         * This allows us to do an exact + partial match first. If that yields no results,
         * fall back to fuzzy results.
         */
        val searchMatchStrategies = searchArguments.searchMatchStrategies
        searchMatchStrategies.forEachIndexed { index, matchStrategies ->
            val searchResult = filterWallpapers(
                searchArguments,
                allSearchRemixMetadata,
                wallpapers,
                matchStrategies,
            )
            if (searchResult is SearchQueryResult.Results) {
                return searchResult
            }
            if (index == searchMatchStrategies.size - 1) {
                return searchResult
            }
        }

        throw IllegalStateException("Unreachable code")
    }

    @JvmName("searchArtist")
    private suspend fun search(
        searchArguments: SearchArguments,
        allSearchArtistMetadata: List<SearchArtistMetadata>,
        artists: List<Artist>,
    ): SearchQueryResult {

        /**
         * Iterate through each search match strategy until a result is found.
         *
         * This allows us to do an exact + partial match first. If that yields no results,
         * fall back to fuzzy results.
         */
        val searchMatchStrategies = searchArguments.searchMatchStrategies
        searchMatchStrategies.forEachIndexed { index, matchStrategies ->
            val searchResult = filterArtists(
                searchArguments,
                allSearchArtistMetadata,
                artists,
                matchStrategies,
            )
            if (searchResult is SearchQueryResult.Results) {
                return searchResult
            }
            if (index == searchMatchStrategies.size - 1) {
                return searchResult
            }
        }

        throw IllegalStateException("Unreachable code")
    }

    @JvmName("searchFolder")
    private suspend fun search(
        searchArguments: SearchArguments,
        allSearchFolderMetadata: List<SearchFolderMetadata>,
        folders: List<Folder>,
    ): SearchQueryResult {

        /**
         * Iterate through each search match strategy until a result is found.
         *
         * This allows us to do an exact + partial match first. If that yields no results,
         * fall back to fuzzy results.
         */
        val searchMatchStrategies = searchArguments.searchMatchStrategies
        searchMatchStrategies.forEachIndexed { index, matchStrategies ->
            val searchResult = filterFolders(
                searchArguments,
                allSearchFolderMetadata,
                folders,
                matchStrategies,
            )
            if (searchResult is SearchQueryResult.Results) {
                return searchResult
            }
            if (index == searchMatchStrategies.size - 1) {
                return searchResult
            }
        }

        throw IllegalStateException("Unreachable code")
    }

    private suspend fun search(
        searchArguments: SearchArguments,
        searchQueryData: SearchQueryData,
    ): SearchQueryResult {
        val wallpaperItems = searchQueryData.allWallpaperItems
        val remixMetadata = searchQueryData.allSearchRemixMetadata
        val allArtists = searchQueryData.allArtists
        val artistMetadata = searchQueryData.allSearchArtistMetadata
        val allFolders = searchQueryData.allFolders
        val folderMetadata = searchQueryData.allSearchFolderMetadata

        val singlesResults = if (!searchArguments.searchSingles) {
            SearchQueryResult.Inactive
        } else {
            val allSingles = wallpaperItems.singles
            if (allSingles != null) {
                search(searchArguments, remixMetadata, allSingles)
            } else {
                SearchQueryResult.NoResults
            }
        }

        val tracksResults = if (!searchArguments.searchTracks) {
            SearchQueryResult.Inactive
        } else {
            val allTracks = wallpaperItems.tracks
            if (allTracks != null) {
                search(searchArguments, remixMetadata, allTracks)
            } else {
                SearchQueryResult.NoResults
            }
        }

        val artistResults = if (!searchArguments.searchArtists) {
            SearchQueryResult.Inactive
        } else {
            if (allArtists.isNotEmpty()) {
                search(searchArguments, artistMetadata, allArtists)
            } else {
                SearchQueryResult.NoResults
            }
        }

        val folderResults = if (!searchArguments.searchFolders) {
            SearchQueryResult.Inactive
        } else {
            if (allFolders.isNotEmpty()) {
                search(searchArguments, folderMetadata, allFolders)
            } else {
                SearchQueryResult.NoResults
            }
        }

        return singlesResults
            .combineSearchQueryResultUnion(tracksResults, artistResults, folderResults)
    }

    fun search(searchArguments: SearchArguments?): Flow<SearchQueryResult> {
        return flow {
            if (searchArguments == null) {
                emit(SearchQueryResult.Inactive)
                return@flow
            }

            emit(SearchQueryResult.Loading)

            data
                .filterNotNull()
                .collect { data ->
                    val results = search(searchArguments, data)
                    emit(results)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun search(searchArguments: List<SearchArguments>): Flow<SearchQueryResult> {
        if (searchArguments.isEmpty()) {
            return flowOf(SearchQueryResult.Inactive)
        }

        Log.d("[FlowSearch] search(): searchArguments: $searchArguments")
        return searchArguments
            .asFlow()
            .flatMapMerge { search(it) }
            .scan<SearchQueryResult, SearchQueryResult>(SearchQueryResult.Loading) { acc, result ->
                acc.combineSearchQueryResultUnion(result)
                    .distinct()
            }
            .flatMapConcat { patchedResult ->
                searchQueryResultPatcher.patch(patchedResult)
            }
            .onStart {
                Log.d("[FlowSearch] search(): onStart")
                emit(SearchQueryResult.Loading)
            }
    }
}