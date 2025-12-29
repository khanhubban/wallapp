package wallapp.wallpaper.current

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.RemixId
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.system.wallpaper.SystemWallpaperDestination

object CurrentWallpaperManagerNoOp : CurrentWallpaperManager {

    override val enabled: Boolean
        get() = false

    override val currentLockScreenWallpaperInfo: Flow<CurrentWallpaperInfo?> = flowOf(null)
    override val currentHomeScreenWallpaperInfo: Flow<CurrentWallpaperInfo?> = flowOf(null)

    override suspend fun updateCurrentWallpaper(
        id: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        destination: SystemWallpaperDestination,
        doUpdate: suspend () -> Unit,
    ) {
        doUpdate()
    }
}