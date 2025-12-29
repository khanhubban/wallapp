package wallapp.wallpaper.cache

import wallapp.data.DataHandle

sealed class WallpaperCacheStatus {
    data class Cached(val dataHandle: DataHandle) : WallpaperCacheStatus()
    data object NotCached : WallpaperCacheStatus()
    data object Loading: WallpaperCacheStatus()
}