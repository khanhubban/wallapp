package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.FolderId
import wallapp.data.artist.ArtistState
import wallapp.data.artist.ArtistStateRepository
import wallapp.data.folder.FolderState
import wallapp.data.folder.FolderStateRepository
import wallapp.search.filter.SearchFilterArbitrator
import wallapp.search.model.SearchFilterMatch
import wallapp.util.combine
import kotlin.jvm.JvmName

class SearchQueryResultPatcherDefault(
    private val artistStateRepository: ArtistStateRepository,
    private val folderStateRepository: FolderStateRepository,
) : SearchQueryResultPatcher {

    override fun patch(searchQueryResult: SearchQueryResult): Flow<SearchQueryResult> {
        if (searchQueryResult !is SearchQueryResult.Results) {
            return flowOf(searchQueryResult)
        }
        val curators: List<SearchQueryItemResult.ResultCurator>? =
            searchQueryResult.itemResults.curators
        if (curators.isNullOrEmpty()) {
            return flowOf(searchQueryResult)
        }

        val folders = curators.filter { it.id is FolderId }
        val artists = curators.filter { it.id is ArtistId }

        return combine(
            folderStateRepository.folderStates,
            artistStateRepository.artistStates,
        ) { folderStates, artistStates ->
            val folderResults = getAdditionalWallpapers(folders, folderStates)
            val artistResults = getAdditionalWallpapers(artists, artistStates)

            searchQueryResult.appendResults(folderResults, artistResults)
        }
    }

    private fun SearchQueryResult.Results.appendResults(
        folderResults: List<SearchQueryItemResult.ResultWallpaper>,
        artistResults: List<SearchQueryItemResult.ResultWallpaper>,
    ): SearchQueryResult {
        val wallpaperResults = (itemResults.wallpapers ?: emptyList())
        val combinedNewResults = (folderResults + artistResults)
            .let { combined ->
                if (combined.isNotEmpty()) {
                    combined.filter { combinedResult ->
                        combinedResult.id !in wallpaperResults.map { it.id }
                    }
                } else {
                    emptyList()
                }
            }
        val combinedResults = (itemResults + combinedNewResults)
            .sortedByDescending { it.searchFilterMatch.weight }
        return SearchQueryResult.Results(itemResults = combinedResults)
    }

    @JvmName("getAdditionalWallpapersFolder")
    private fun getAdditionalWallpapers(
        folders: List<SearchQueryItemResult.ResultCurator>,
        folderStates: List<FolderState>,
    ): List<SearchQueryItemResult.ResultWallpaper> {
        return folders.map { resultCurator ->
            val folderState = folderStates.find { it.folder.id == resultCurator.id }
            if (folderState != null) {
                getAdditionalWallpapersForFolder(resultCurator, folderState)
            } else {
                emptyList()
            }
        }.flatten()
    }

    private fun getAdditionalWallpapersForFolder(
        resultCurator: SearchQueryItemResult.ResultCurator,
        folderState: FolderState,
    ): List<SearchQueryItemResult.ResultWallpaper> {
        return folderState.wallpaperStates
            .map { wallpaperState ->
                SearchQueryItemResult.ResultWallpaper(
                    wallpaper = wallpaperState.wallpaper,
                    searchFilterMatch = SearchFilterMatch.ManualPlacement(
                        id = resultCurator.id,
                        weight = SearchFilterArbitrator.Weight.CuratorMatch,
                    ),
                    searchMetadata = resultCurator.searchMetadata,
                )
            }
    }

    @JvmName("getAdditionalWallpapersArtist")
    private fun getAdditionalWallpapers(
        artists: List<SearchQueryItemResult.ResultCurator>,
        artistStates: List<ArtistState>,
    ): List<SearchQueryItemResult.ResultWallpaper> {
        return artists.map { resultCurator ->
            val artistState = artistStates.find { it.artist.id == resultCurator.id }
            if (artistState != null) {
                getAdditionalWallpapersForArtist(resultCurator, artistState)
            } else {
                emptyList()
            }
        }.flatten()
    }

    private fun getAdditionalWallpapersForArtist(
        resultCurator: SearchQueryItemResult.ResultCurator,
        artistState: ArtistState,
    ): List<SearchQueryItemResult.ResultWallpaper> {
        return artistState.feedItems
            ?.map { wallpaper ->
                SearchQueryItemResult.ResultWallpaper(
                    wallpaper = wallpaper,
                    searchFilterMatch = SearchFilterMatch.ManualPlacement(
                        id = resultCurator.id,
                        weight = SearchFilterArbitrator.Weight.CuratorMatch,
                    ),
                    searchMetadata = resultCurator.searchMetadata,
                )
            }
            ?: emptyList()
    }
}