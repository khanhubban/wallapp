package wallapp.search.content

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.Id.RemixId
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFolderMetadata
import wallapp.search.model.SearchRemixMetadata

interface SearchContentRepository {

    val allSearchRemixMetadata: Flow<List<SearchRemixMetadata>>
    fun getSearchRemixMetadata(id: RemixId): Flow<SearchRemixMetadata?>

    val allSearchArtistMetadata: Flow<List<SearchArtistMetadata>>
    fun getSearchArtistMetadata(id: ArtistId): Flow<SearchArtistMetadata?>

    val allSearchFolderMetadata: Flow<List<SearchFolderMetadata>>
    fun getSearchFolderMetadata(id: FolderId): Flow<SearchFolderMetadata?>
}