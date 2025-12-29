package wallapp.wallpaper.history

import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix

interface WallpaperHistoryManager {

    val wallpaperHistoryStack: List<OrderedWallpaper>

    fun clearHistory()

    fun onWallpaperChanged(value: WallpaperRemix)

    fun popCurrent(): RemixId?
    fun popPreviousRemixOrWallpaper(): RemixId?
    fun popPreviousRemix(): RemixId?
    fun popPreviousWallpaper(): RemixId?
}