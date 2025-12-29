package wallapp.system.wallpaper

import android.app.WallpaperManager

object AndroidWallpaperMapper {

    val SystemWallpaperDestination.toWhich: Int
        get() = when (this) {
            SystemWallpaperDestination.LockScreen -> WallpaperManager.FLAG_LOCK
            SystemWallpaperDestination.HomeScreen -> WallpaperManager.FLAG_SYSTEM
            SystemWallpaperDestination.Both -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
        }
}