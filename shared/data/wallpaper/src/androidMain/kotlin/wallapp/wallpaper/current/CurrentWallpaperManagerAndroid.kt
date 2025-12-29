package wallapp.wallpaper.current

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import wallapp.activity.ActivityLifecycleListener
import wallapp.appstate.AppState
import wallapp.system.wallpaper.SystemWallpaperManager

class CurrentWallpaperManagerAndroid(
    context: Context,
    appState: AppState,
    systemWallpaperManager: SystemWallpaperManager,
    coroutineScopeMain: CoroutineScope,
    coroutineScopeIo: CoroutineScope,
) : CurrentWallpaperManagerDefault(
    appState,
    systemWallpaperManager,
    coroutineScopeMain,
    coroutineScopeIo,
) {
    private val activityCallbacks = object : ActivityLifecycleListener.Callbacks() {
        override fun onActivityPostStarted(activity: Activity) {
            super.onActivityPostStarted(activity)
            refresh()
        }
    }

    init {
        ActivityLifecycleListener(context, activityCallbacks)
    }
}