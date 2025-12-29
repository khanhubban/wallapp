package wallapp.wallpapers

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import wallapp.system.wallpaper.SystemWallpaperManagerAndroid

class WallpaperAppChangeObserver(
    private val systemWallpaperManager: SystemWallpaperManagerAndroid
) :
    DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        systemWallpaperManager.invalidateCurrentWallpaperApp()
    }

    override fun onPause(owner: LifecycleOwner) {
        systemWallpaperManager.invalidateCurrentWallpaperApp()
    }

}