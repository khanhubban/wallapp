package wallapp.system.wallpaper

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SystemWallpaperManagerNoOp : SystemWallpaperManager {

    override val isCurrentSystemWallpaperApp: StateFlow<Boolean?> = MutableStateFlow(false)

    override fun getWallpaperId(destination: SystemWallpaperDestination): Int? = null

    override suspend fun setStaticWallpaper(bitmap: Any, destination: SystemWallpaperDestination): Boolean = false
}