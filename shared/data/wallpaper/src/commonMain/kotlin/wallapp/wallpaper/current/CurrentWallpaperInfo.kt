package wallapp.wallpaper.current

import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.StaticWallpaperSize

data class CurrentWallpaperInfo(
    val wallpaperId: WallpaperId,
    val staticWallpaperSize: StaticWallpaperSize?,
)
