package wallapp.wallpaper.set

import kotlinx.coroutines.CoroutineScope
import wallapp.ad.AdManager
import wallapp.content.model.Id.RemixId
import wallapp.navigation.AppNavigator
import wallapp.system.navigation.SystemNavigator
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.wallpaper.app.AppWallpaperManager
import wallapp.wallpaper.live.LiveWallpaperManager

class SetWallpaperManagerDefault(
    private val appWallpaperManager: AppWallpaperManager,
    private val liveWallpaperManager: LiveWallpaperManager,
    private val systemWallpaperManager: SystemWallpaperManager,
    private val systemNavigator: SystemNavigator,
    private val appNavigator: AppNavigator,
    private val adManager: AdManager,
    coroutineScopeMain: CoroutineScope,
) : SetWallpaperManager {

//    private val currentWallpaper = MutableStateFlow<WallpaperRemix?>(null)

    /**
     * Returns [currentWallpaper], but only if the app is the current system live wallpaper.
     */
//    val systemWallpaperRemix: Flow<WallpaperRemix?> = combine(
//        liveWallpaperManager.currentWallpaper,
//        systemWallpaperManager.isCurrentSystemWallpaperApp,
//    ) { currentWallpaper, isCurrentSystemWallpaperApp ->
//        if (isCurrentSystemWallpaperApp == true) {
//            currentWallpaper
//        } else {
//            null
//        }
//    }

    private val isCurrentSystemWallpaperApp: Boolean
        get() = systemWallpaperManager.isCurrentSystemWallpaperApp.value == true
    private val isLiveWallpaperAvailable: Boolean
        get() = liveWallpaperManager.isLiveWallpaperAvailable

    override fun setWallpaper(remixId: RemixId, setWallpaperMode: SetWallpaperMode, reason: String) {
        when (setWallpaperMode) {
            SetWallpaperMode.Static -> setStaticWallpaper(remixId)
            SetWallpaperMode.Live -> setLiveWallpaper(remixId)
            SetWallpaperMode.Arbitrated -> {
                if (isLiveWallpaperAvailable) {
                    setLiveWallpaper(remixId)
                } else {
                    setStaticWallpaper(remixId)
                }
            }
        }
    }

    private fun setStaticWallpaper(remixId: RemixId) {
        liveWallpaperManager.updateCurrentWallpaper(
            remixId,
            null,
            "Use wallpaper clicked in preview screen",
            setAsStatic = true,
        )
    }

    private fun setLiveWallpaper(remixId: RemixId) {
        require(isLiveWallpaperAvailable)
        if (isCurrentSystemWallpaperApp) {
//            if (showAds) {
//                appNavigator.showRewardAd(onRewardEarned = {}, onRewardError = {})
//            }

            liveWallpaperManager.updateCurrentWallpaper(
                remixId,
                null,
                "Use wallpaper clicked in preview screen",
            )
        } else {
            appWallpaperManager.updateCurrentDisplayWallpaper(remixId)
            systemNavigator.toSystemSetAppAsLiveWallpaper()
        }
    }

//    init {
//        liveWallpaperManager.currentWallpaper.collectIn(coroutineScopeMain) {
//            currentWallpaper.value = it
//        }
//    }
}