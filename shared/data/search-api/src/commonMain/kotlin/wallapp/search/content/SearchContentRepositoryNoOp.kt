package wallapp.search.content

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFolderMetadata
import wallapp.search.model.SearchRemixMetadata

object SearchContentRepositoryNoOp : SearchContentRepository {
    override val allSearchRemixMetadata: Flow<List<SearchRemixMetadata>> = flowOf(emptyList())

    override fun getSearchRemixMetadata(id: Id.RemixId): Flow<SearchRemixMetadata?> = flowOf(null)

    override val allSearchArtistMetadata: Flow<List<SearchArtistMetadata>> = flowOf(emptyList())

    override fun getSearchArtistMetadata(id: Id.ArtistId): Flow<SearchArtistMetadata?> = flowOf(null)

    override val allSearchFolderMetadata: Flow<List<SearchFolderMetadata>> = flowOf(emptyList())

    override fun getSearchFolderMetadata(id: Id.FolderId): Flow<SearchFolderMetadata?> = flowOf(null)
}