package wallapp.wallpaper.live

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.wallpaper.history.WallpaperHistoryManager


interface LiveWallpaperManager {

    val isLiveWallpaperAvailable: Boolean

    val isReady: Flow<Boolean>

    val currentWallpaper: Flow<WallpaperRemix?>
    fun getCurrentWallpaper(): WallpaperRemix?

    fun updateCurrentWallpaper(
        remixId: RemixId,
        nextRemixId: RemixId?,
        reason: String, /* for logging purposes */
        setAsStatic: Boolean = false,
        showToast: Boolean = false,
        forceShowDebugNotification: Boolean = false
    )

    fun updateCurrentWallpaper(
        wallpaperRemix: WallpaperRemix,
        nextRemixId: RemixId?,
        reason: String, /* for logging purposes */
        setAsStatic: Boolean = false,
        showToast: Boolean = false,
        forceShowDebugNotification: Boolean = false
    )

    val nextWallpaper: Flow<WallpaperRemix?>

    fun updateNextWallpaper(remixId: RemixId?)

    fun useNextWallpaper(setNewRandomNextWallpaper: Boolean)

    /**
     * Returns true if a switch to dark remix was made.
     */
    fun checkSwitchToDarkRemix(alsoSwitchPreview: Boolean = false): Boolean

    fun setDefaultWallpaper(message: String)
    fun revertWallpaperChange()

    val historyManager: WallpaperHistoryManager
}