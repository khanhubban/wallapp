package wallapp.data.showcase

import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId

interface ShowcaseRepositoryHighlightsConfig {
    val highlightArtist: StateFlow<ArtistId>
    val highlightCollectionOfTheWeek: StateFlow<CategoryId>
    val highlightJustAdded: StateFlow<CategoryId>
    val highlightMostPopular: StateFlow<CategoryId>
    val highlightWallpaperOfTheWeek: StateFlow<RemixId>
    val showPlus: StateFlow<Boolean>
}