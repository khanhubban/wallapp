package wallapp.wallpaper.download

import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.StaticWallpaperSize

class WallpaperDownloadUrlCache {

    private val cache = mutableMapOf<Pair<WallpaperId, StaticWallpaperSize>, String>()

    fun put(wallpaperId: WallpaperId, staticWallpaperSize: StaticWallpaperSize, url: String) {
        cache[wallpaperId to staticWallpaperSize] = url
    }

    fun get(wallpaperId: WallpaperId, staticWallpaperSize: StaticWallpaperSize): String? {
        return cache[wallpaperId to staticWallpaperSize]
    }
}