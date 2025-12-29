package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appstate.AppState

class WallpaperSystemPhotoStatusCacheDefaultDataDefault(
    val appState: AppState,
) : WallpaperSystemPhotoStatusCacheDefaultData {

    override val allCache: MutableStateFlow<String>
        get() = appState.systemPhotoStatusCache
}