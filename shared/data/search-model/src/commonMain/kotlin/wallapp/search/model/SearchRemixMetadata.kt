package wallapp.search.model

import wallapp.content.model.Id.RemixId
import wallapp.string.quote

data class SearchRemixMetadata(
    val remixId: RemixId,
    val artistNames: List<SearchFilterEntry>,
    val title: SearchFilterEntry,
    val collectionTitle: SearchFilterEntry?,
    val categories: List<SearchFilterEntry>,
    val tags: List<SearchFilterEntry>,
    val colors: List<SearchFilterEntry>,
    val searchTerms: List<SearchFilterEntry>,  // Sorted by most appropriate first
    val titleSuggestions: List<SearchFilterEntry>,
    val description: SearchFilterEntry?,
) {

    val debugString: String get() {
        return "[SearchRemixMetadata]\n  remixId: ${remixId.name.quote()}\n" +
                "  artistNames: ${artistNames.joinToString { it.toString(includeId = false) }}\n" +
                "  title: ${title.toString(includeId = false)}\n" +
                "  collectionTitle: ${collectionTitle?.toString(includeId = false)}\n" +
                "  categories: ${categories.joinToString { it.toString(includeId = false) }}\n" +
                "  tags: ${tags.joinToString { it.toString(includeId = false) }}\n" +
                "  colors: ${colors.joinToString { it.toString(includeId = false) }}\n" +
                "  searchTerms: ${searchTerms.joinToString { it.toString(includeId = false) }}\n"
//                "titleSuggestions: ${titleSuggestions.joinToString()}, " +
//                "description: ${description.quote()}"
    }
}

fun SearchRemixMetadataPreset(
    remixId: RemixId,
    artistNames: List<SearchFilterEntry> = emptyList(),
    title: SearchFilterEntry = SearchFilterEntryPreset(remixId, ""),
    collectionTitle: SearchFilterEntry? = null,
    searchTerms: List<SearchFilterEntry> = emptyList(),
    categories: List<SearchFilterEntry> = emptyList(),
    tags: List<SearchFilterEntry> = emptyList(),
    colors: List<SearchFilterEntry> = listOf(SearchFilterEntryPreset(remixId, SearchColor.Dark.key)),
    titleSuggestions: List<SearchFilterEntry> = emptyList(),
    description: SearchFilterEntry = SearchFilterEntryPreset(remixId, ""),
) = SearchRemixMetadata(
    remixId = remixId,
    artistNames = artistNames,
    title = title,
    collectionTitle = collectionTitle,
    categories = categories,
    tags = tags,
    colors = colors,
    searchTerms = searchTerms,
    titleSuggestions = titleSuggestions,
    description = description,
)