package wallapp.search.model

import wallapp.content.model.Id.ArtistId

data class SearchArtistMetadata(
    val artistId: ArtistId,
    val artistNames: List<SearchFilterEntry>,
)


fun SearchArtistMetadataPreset(
    artistId: ArtistId,
    artistNames: List<SearchFilterEntry> = emptyList(),
) = SearchArtistMetadata(
    artistId = artistId,
    artistNames = artistNames,
)