package wallapp.wallpaper.app

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix

/**
 * Manages the wallpaper that displays in the app itself (such as on the feed, on a preview, etc.)
 *
 * See [CurrentWallpaperManager] for managing the user's current wallpaper selection.
 */
interface AppWallpaperManager {

    val currentDisplayWallpaper: Flow<WallpaperRemix?>
    fun updateCurrentDisplayWallpaper(remixId: RemixId)

    fun onConfigurationChanged()
}
