package wallapp.wallpaper.app

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.model.Id
import wallapp.content.model.WallpaperRemix

class AppWallpaperManagerPreset : AppWallpaperManager {
    override val currentDisplayWallpaper: MutableStateFlow<WallpaperRemix?> = MutableStateFlow(null)

    override fun updateCurrentDisplayWallpaper(remixId: Id.RemixId) { }

    override fun onConfigurationChanged() { }
}