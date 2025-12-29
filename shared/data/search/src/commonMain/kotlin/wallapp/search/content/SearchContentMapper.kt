package wallapp.search.content

import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.RemixId
import wallapp.search.model.NetworkSearchArtistMetadata
import wallapp.search.model.NetworkSearchEntry
import wallapp.search.model.NetworkSearchFolderMetadata
import wallapp.search.model.NetworkSearchMetadata
import wallapp.search.model.NetworkSearchRemixMetadata
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFilterEntry
import wallapp.search.model.SearchFolderMetadata
import wallapp.search.model.SearchRemixMetadata

object SearchContentMapper {

    private fun List<NetworkSearchEntry>.mapNormalized(id: Id, scaler: Float): List<SearchFilterEntry> {
        return map { SearchFilterEntry(id, it.term, it.relevance / scaler) }
    }

    val NetworkSearchMetadata.allSearchRemixMetadata: List<SearchRemixMetadata>
        get() = remixMetadata.map { it.searchRemixMetadata }

    val NetworkSearchRemixMetadata.searchRemixMetadata: SearchRemixMetadata
        get() {
            val id = RemixId(remixId)
            return SearchRemixMetadata(
                remixId = id,
                artistNames = artistNames.map { SearchFilterEntry(id, it, SearchFilterEntry.MaxRelevance) },
                title = title.let { SearchFilterEntry(id, it, SearchFilterEntry.MaxRelevance) },
                collectionTitle = collectionTitle?.let { SearchFilterEntry(id, it, SearchFilterEntry.MaxRelevance) },
                colors = colors.map { SearchFilterEntry(id, it) },
                categories = styles.map { SearchFilterEntry(id, it) },
                tags = tags.map { SearchFilterEntry(id, it) },
                searchTerms = searchTerms.map { SearchFilterEntry(id, it) },
                titleSuggestions = titleSuggestions.map { SearchFilterEntry(id, it) },
                description = description?.let { SearchFilterEntry(id, it) },
            )
        }

    val NetworkSearchMetadata.allSearchArtistMetadata: List<SearchArtistMetadata>
        get() = artistMetadata.map { it.searchArtistMetadata }

    val NetworkSearchArtistMetadata.searchArtistMetadata: SearchArtistMetadata
        get() {
            val id = ArtistId(artistId)
            return SearchArtistMetadata(
                artistId = id,
                artistNames = this.names.map { SearchFilterEntry(id, it) },
            )
        }

    val NetworkSearchMetadata.allSearchFolderMetadata: List<SearchFolderMetadata>
        get() = folderMetadata.map { it.searchFolderMetadata }

    val NetworkSearchFolderMetadata.searchFolderMetadata: SearchFolderMetadata
        get() {
            val id = Id.FolderId(folderId)
            return SearchFolderMetadata(
                folderId = id,
                folderNames = this.names.map { SearchFilterEntry(id, it) },
            )
        }
}