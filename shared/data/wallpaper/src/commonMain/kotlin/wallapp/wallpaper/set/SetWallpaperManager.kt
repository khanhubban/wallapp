package wallapp.wallpaper.set

import wallapp.content.model.Id.RemixId

/**
 * Responsible for setting the wallpaper, either as static or a live wallpaper. With live wallpapers
 * not being offered at this time, use of [wallapp.wallpaper.static.StaticWallpaperManager] is
 * generally preferred.
 */
interface SetWallpaperManager {

    fun setWallpaper(
        remixId: RemixId,
        setWallpaperMode: SetWallpaperMode,
        reason: String, /* for logging */
    )
}