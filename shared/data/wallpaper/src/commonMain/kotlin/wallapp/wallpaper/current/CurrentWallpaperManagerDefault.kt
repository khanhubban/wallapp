package wallapp.wallpaper.current

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.appstate.AppState
import wallapp.content.model.Id.RemixId
import wallapp.coroutine.collectIn
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.log.Logger
import wallapp.system.wallpaper.SystemWallpaperDestination
import wallapp.system.wallpaper.SystemWallpaperManager

open class CurrentWallpaperManagerDefault(
    private val appState: AppState,
    private val systemWallpaperManager: SystemWallpaperManager,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : CurrentWallpaperManager {

    companion object {
        val Log = Logger("CurrentWallpaperManager")
    }

    override val enabled: Boolean
        get() = true

    fun refresh() {
        coroutineScopeIo.launch {
            refreshSystemWallpaperIds()
        }
    }

    private suspend fun refreshSystemWallpaperIds() {
        Log.d("refreshSystemWallpaperIds")
        currentLockScreenSystemWallpaperId.value = systemWallpaperManager
            .getWallpaperId(SystemWallpaperDestination.LockScreen)
        currentHomeScreenSystemWallpaperId.value = systemWallpaperManager
            .getWallpaperId(SystemWallpaperDestination.HomeScreen)
    }

    private val currentLockScreenSystemWallpaperId: MutableStateFlow<Int?> =
        MutableStateFlow(null)
    private val lastLockScreenRawData: MutableStateFlow<String>
        get() = appState.lastLockScreenWallpaperData
    private val lastLockScreenWallpaperData: StateFlow<LastWallpaperData?> =
        lastLockScreenRawData.map {
            LastWallpaperData.from(it, SystemWallpaperDestination.LockScreen)
        }.stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, null)

    override val currentLockScreenWallpaperInfo: Flow<CurrentWallpaperInfo?> = combine(
        currentLockScreenSystemWallpaperId, lastLockScreenWallpaperData
    ) { currentSystemWallpaperId, lastData ->
        if (currentSystemWallpaperId != null && currentSystemWallpaperId == lastData?.systemWallpaperId) {
            lastData.currentWallpaperInfo
        } else {
            null
        }
    }

    private val currentHomeScreenSystemWallpaperId: MutableStateFlow<Int?> =
        MutableStateFlow(null)
    private val lastHomeScreenRawData: MutableStateFlow<String>
        get() = appState.lastHomeScreenWallpaperData
    private val lastHomeScreenWallpaperData: StateFlow<LastWallpaperData?> =
        lastHomeScreenRawData.map {
            LastWallpaperData.from(it, SystemWallpaperDestination.HomeScreen)
        }.stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, null)
    override val currentHomeScreenWallpaperInfo: Flow<CurrentWallpaperInfo?> = combine(
        currentHomeScreenSystemWallpaperId, lastHomeScreenWallpaperData
    ) { currentSystemWallpaperId, lastData ->
        if (currentSystemWallpaperId != null && currentSystemWallpaperId == lastData?.systemWallpaperId) {
            lastData.currentWallpaperInfo
        } else {
            null
        }
    }

    override suspend fun updateCurrentWallpaper(
        id: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        destination: SystemWallpaperDestination,
        doUpdate: suspend () -> Unit,
    ) {
        val lockScreenWallpaperIdBefore = systemWallpaperManager.getWallpaperId(SystemWallpaperDestination.LockScreen)
        val homeScreenWallpaperIdBefore = systemWallpaperManager.getWallpaperId(SystemWallpaperDestination.HomeScreen)

        doUpdate()

        val lockScreenWallpaperIdAfter = systemWallpaperManager.getWallpaperId(SystemWallpaperDestination.LockScreen)
        val homeScreenWallpaperIdAfter = systemWallpaperManager.getWallpaperId(SystemWallpaperDestination.HomeScreen)

        if (lockScreenWallpaperIdAfter != lockScreenWallpaperIdBefore
            && lockScreenWallpaperIdAfter != null
            && lockScreenWallpaperIdAfter > -1) {
            Log.d("setStaticWallpaper(destination=$destination): " +
                    "lockScreenWallpaperId: $lockScreenWallpaperIdBefore -> $lockScreenWallpaperIdAfter")
            lastLockScreenRawData.value = LastWallpaperData(id, staticWallpaperSize, lockScreenWallpaperIdAfter, destination).data
        }
        if (homeScreenWallpaperIdAfter != homeScreenWallpaperIdBefore
            && homeScreenWallpaperIdAfter != null
            && homeScreenWallpaperIdAfter > -1) {
            Log.d("setStaticWallpaper(destination=$destination): " +
                    "homeScreenWallpaperId: $homeScreenWallpaperIdBefore -> $homeScreenWallpaperIdAfter")
            lastHomeScreenRawData.value = LastWallpaperData(id, staticWallpaperSize, homeScreenWallpaperIdAfter, destination).data
        }

        currentLockScreenSystemWallpaperId.value = lockScreenWallpaperIdAfter
        currentHomeScreenSystemWallpaperId.value = homeScreenWallpaperIdAfter
    }

    init {
        refresh()

//        currentHomeScreenSystemWallpaperId.collectIn(coroutineScopeIo) {
//            Log.d("currentHomeScreenId: $it")
//        }
        currentHomeScreenWallpaperInfo.collectIn(coroutineScopeIo) {
            Log.d("currentHomeScreenWallpaperId: $it")
        }
//        lastHomeScreenWallpaperData.collectIn(coroutineScopeIo) {
//            Log.d("lastHomeScreenWallpaperData: $it")
//        }
//        currentLockScreenSystemWallpaperId.collectIn(coroutineScopeIo) {
//            Log.d("currentLockScreenId: $it")
//        }
        currentLockScreenWallpaperInfo.collectIn(coroutineScopeIo) {
            Log.d("currentLockScreenWallpaperId: $it")
        }
//        lastLockScreenWallpaperData.collectIn(coroutineScopeIo) {
//            Log.d("lastLockScreenWallpaperData: $it")
//        }
    }
}
