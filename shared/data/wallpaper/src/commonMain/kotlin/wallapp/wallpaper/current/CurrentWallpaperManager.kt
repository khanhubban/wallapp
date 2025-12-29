package wallapp.wallpaper.current

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.system.wallpaper.SystemWallpaperDestination

interface CurrentWallpaperManager {

    val enabled: Boolean

    val currentLockScreenWallpaperInfo: Flow<CurrentWallpaperInfo?>
    val currentHomeScreenWallpaperInfo: Flow<CurrentWallpaperInfo?>

    suspend fun updateCurrentWallpaper(
        id: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        destination: SystemWallpaperDestination,
        doUpdate: suspend () -> Unit,
    )
}