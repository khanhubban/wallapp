package wallapp.wallpaper.live

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.wallpaper.history.WallpaperHistoryManager
import wallapp.wallpaper.history.WallpaperHistoryManagerNoOp

class LiveWallpaperManagerNoOp : LiveWallpaperManager {

    override val isLiveWallpaperAvailable: Boolean
        get() = false

    override val isReady = MutableStateFlow(false)

    override val historyManager: WallpaperHistoryManager = WallpaperHistoryManagerNoOp

    override val currentWallpaper = MutableStateFlow<WallpaperRemix?>(null)
    override fun getCurrentWallpaper(): WallpaperRemix? = currentWallpaper.value

    override val nextWallpaper = MutableStateFlow<WallpaperRemix?>(null)

    override fun updateCurrentWallpaper(
        remixId: RemixId,
        nextRemixId: RemixId?,
        reason: String,
        setAsStatic: Boolean,
        showToast: Boolean,
        forceShowDebugNotification: Boolean,
    ) {
        // no op
    }

    override fun updateCurrentWallpaper(
        wallpaperRemix: WallpaperRemix,
        nextRemixId: RemixId?,
        reason: String,
        setAsStatic: Boolean,
        showToast: Boolean,
        forceShowDebugNotification: Boolean,
    ) {
        currentWallpaper.value = wallpaperRemix
    }

    override fun updateNextWallpaper(remixId: RemixId?) {
        // no op
    }

    override fun useNextWallpaper(setNewRandomNextWallpaper: Boolean) {
        // no op
    }

    override fun checkSwitchToDarkRemix(alsoSwitchPreview: Boolean): Boolean {
        return false
    }

    override fun setDefaultWallpaper(message: String) {
        // no op
    }

    override fun revertWallpaperChange() {
        // no op
    }
}