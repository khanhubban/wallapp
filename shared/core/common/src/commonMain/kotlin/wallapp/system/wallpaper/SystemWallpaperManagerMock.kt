package wallapp.system.wallpaper

import kotlinx.coroutines.flow.MutableStateFlow

class SystemWallpaperManagerMock(
    isCurrentSystemWallpaperApp: Boolean = true,
) : SystemWallpaperManager {

    override val isCurrentSystemWallpaperApp: MutableStateFlow<Boolean> =
        MutableStateFlow(isCurrentSystemWallpaperApp)

    override fun getWallpaperId(destination: SystemWallpaperDestination): Int? = null

    override suspend fun setStaticWallpaper(bitmap: Any, destination: SystemWallpaperDestination): Boolean = false
}