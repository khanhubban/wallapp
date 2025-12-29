package wallapp.system.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appstate.AppState
import wallapp.instantapp.InstantAppManager
import wallapp.log.Log
import wallapp.process.Process
import wallapp.system.platform.PlatformFeature
import wallapp.time.TimeRepository
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import android.app.WallpaperManager as WallpaperManagerAndroid

class SystemWallpaperManagerAndroid(
    private val context: Context,
    private val androidWallpaperManager: AndroidWallpaperManager,
    private val appState: AppState,
    private val timeRepository: TimeRepository,
    private val instantAppManager: InstantAppManager,
    private val process: Process,
): SystemWallpaperManager {

    companion object {
        val WorkAroundNullWallpaperInfo = PlatformFeature.CurrentLiveWallpaperRequiresWorkaround
    }

    private val wallpaperManager: WallpaperManagerAndroid?
        get() = if (instantAppManager.isInstantApp) {
            null
        } else {
            WallpaperManagerAndroid.getInstance(context)
        }

    override val isCurrentSystemWallpaperApp = MutableStateFlow<Boolean?>(null)

    init {
        appState.isCurrentSystemWallpaperApp.subscribe(skipFirst = false) {
            Log.d("[${process.processNameSuffix}] appState.isCurrentSystemWallpaperApp: $it")
            isCurrentSystemWallpaperApp.value = it
        }
        updateCurrentSystemWallpaperApp()
    }

    fun invalidateCurrentWallpaperApp() {
        /**
         * As per #2830, [setCurrentSystemWallpaperApp] is the only reliable means of determining
         * the current wallpaper app when running Android 13 or later.
         */
        if (WorkAroundNullWallpaperInfo) return

        updateCurrentSystemWallpaperApp()
    }

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    private val inferCurrentSystemWallpaperApp: Boolean
        get() = Build.VERSION.SDK_INT >= 33

    private fun updateCurrentSystemWallpaperApp() {
        /**
         * Note: as per #2830, [wallpaperManager?.wallpaperInfo] always returns null on Android 13
         * betas. In this case, we rely on [setCurrentSystemWallpaperApp], called from the wallpaper
         * service to determine if the app is the current live wallpaper.
         */
        if (inferCurrentSystemWallpaperApp) {
            val timeSinceRefresh =
                (timeRepository.currentTime - appState.isCurrentSystemWallpaperAppRefreshTime.value)
                    .toDuration(DurationUnit.MILLISECONDS)
            if (appState.isCurrentSystemWallpaperApp.value && timeSinceRefresh > 5.seconds) {
                Log.d("[${process.processNameSuffix}] Force isCurrentSystemWallpaperApp=false")
                appState.isCurrentSystemWallpaperApp.updateIfNew(false)
            }
        } else {
            // wallpaperInfo is reliable on
            val wallpaperPackageName = wallpaperManager?.wallpaperInfo?.packageName
            Log.d("[${process.processNameSuffix}] Current wallpaperPackageName: $wallpaperPackageName")
            appState.isCurrentSystemWallpaperApp.updateIfNew(wallpaperPackageName == context.packageName)
        }
    }

    fun setCurrentSystemWallpaperApp(isCurrent: Boolean) {
        Log.i("[${process.processNameSuffix}] setCurrentSystemWallpaperApp(): $isCurrent")
        appState.isCurrentSystemWallpaperApp.updateIfNew(isCurrent)
        appState.isCurrentSystemWallpaperAppRefreshTime.update(timeRepository.currentTime)
        updateCurrentSystemWallpaperApp()
    }

    override fun getWallpaperId(destination: SystemWallpaperDestination): Int? {
        return androidWallpaperManager.getWallpaperId(destination)
    }

    override suspend fun setStaticWallpaper(bitmap: Any, destination: SystemWallpaperDestination): Boolean {
        return androidWallpaperManager.setStaticWallpaper(bitmap as Bitmap, destination)
    }
}