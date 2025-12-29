package wallapp.system.wallpaper

import kotlinx.coroutines.flow.StateFlow

interface SystemWallpaperManager {

    val isCurrentSystemWallpaperApp: StateFlow<Boolean?>

    fun getWallpaperId(destination: SystemWallpaperDestination): Int?

    suspend fun setStaticWallpaper(bitmap: Any, destination: SystemWallpaperDestination): Boolean
}
