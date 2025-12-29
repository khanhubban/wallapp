package wallapp.data.showcase

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.WallpaperRemix
import wallapp.data.highlight.Highlights

interface ShowcaseRepository {

    val signUpWallpapers: Flow<List<WallpaperRemix>>

    val exploreHighlights: Flow<Highlights?>
}