package wallapp.data.content

import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.data.highlight.Highlights

interface ContentCacheManager {

    val prefetchWallpaperImagesEnabled: Boolean

    fun prefetch(id: Id, isUiSettled: StateFlow<Boolean>, visibleIndex: Int)

    fun prefetchExplore()

    fun prefetchHomeOnboarding()

    fun prefetchHighlights(highlights: Highlights)
}