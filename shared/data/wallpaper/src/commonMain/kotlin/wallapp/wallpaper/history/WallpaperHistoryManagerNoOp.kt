package wallapp.wallpaper.history

import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix

object WallpaperHistoryManagerNoOp : WallpaperHistoryManager {
    override val wallpaperHistoryStack: List<OrderedWallpaper> = emptyList()

    override fun clearHistory() { }

    override fun onWallpaperChanged(value: WallpaperRemix) { }

    override fun popCurrent(): RemixId? = null

    override fun popPreviousRemixOrWallpaper(): RemixId? = null

    override fun popPreviousRemix(): RemixId? = null

    override fun popPreviousWallpaper(): RemixId? = null
}