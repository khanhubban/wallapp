package wallapp.data.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.data.artist.Artist
import wallapp.data.folder.Folder

interface ModelRepository {

    /**
     * All [WallpaperRemix] and [WallpaperCategory] items.
     *
     * Note: [WallpaperDesign] is NOT included.
     */
    val allWallpaperItems: StateFlow<List<WallpaperItem>>

    val allRemixes: Flow<List<WallpaperRemix>>
//    val allDesigns: Flow<List<WallpaperDesign>>
    val allCategories: Flow<List<WallpaperCategory>>

    val allArtists: Flow<List<Artist>>

    val allFolders: Flow<List<Folder>>

}

/**
 * Intended for use in tests.
 */
suspend fun ModelRepository.suspendUntilNonEmpty() {
    allWallpaperItems.first { it.isNotEmpty() }
}