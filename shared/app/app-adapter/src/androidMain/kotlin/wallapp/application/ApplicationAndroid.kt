package wallapp.application

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import wallapp.appshortcuts.AppShortcutsManager
import wallapp.buildconfig.BuildConfig
import wallapp.coroutine.CoroutineContexts
import wallapp.crashtracking.CrashTrackingHolder.crashTracking
import wallapp.device.state.DeviceState
import wallapp.device.state.DeviceStateSystem
import wallapp.initializer.module.ModuleInitializers
import wallapp.license.cache.LicenseCacheBackup
import wallapp.log.Log
import wallapp.messaging.CloudMessagingManager
import wallapp.monitoring.MonitoringManager
import wallapp.process.Process
import wallapp.process.bridge.ProcessBridge
import wallapp.remoteconfig.RemoteConfig
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.theme.SystemThemeAndroid
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryAndroid
import wallapp.wallpaper.live.LiveWallpaperManager

open class ApplicationAndroid : Application, KoinComponent {

    private val context: Context by inject()

    private val moduleInitializers: ModuleInitializers by inject()

    val systemWallpaperManager: SystemWallpaperManager by inject()
    val liveWallpaperManager: LiveWallpaperManager by inject()

    private val deviceState: DeviceState by inject()

    // In order to have its member variables populated by the time WallpaperService starts
//    private val playlistManager: PlaylistManager by inject()
    // Manages dynamic app shortcuts. Once created, does not need further interfacing with.
    private val appShortcutsManager: AppShortcutsManager by inject()

    private val processBridge: ProcessBridge by inject()
    private val monitoringManager: MonitoringManager by inject()
    private val cloudMessagingManager: CloudMessagingManager by inject()

    private val applicationLifecycleObserver: ApplicationLifecycleObserver by inject()
    private val coroutineContexts: CoroutineContexts by inject()
    private val process: Process by inject()
    private val remoteConfig: RemoteConfig by inject()
    private val licenseCacheBackup: LicenseCacheBackup by inject()
    private val timeRepository: TimeRepository by inject()
    private val systemTheme: SystemThemeAndroid by inject()
    private val buildConfig: BuildConfig by inject()

    fun onCreatePre() {
        initializeFirebase()
        moduleInitializers.initialize()
    }

    fun onCreatePost() {
        if (deviceState.isUserUnlocked) {
            init()
        } else {
            deviceState.registerForUserUnlock {
                init()
            }
        }
        val deviceState = deviceState
        if (deviceState is DeviceStateSystem) {
            deviceState.register(context)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationLifecycleObserver)

        val timeRepository = timeRepository
        if (timeRepository is TimeRepositoryAndroid) {
            timeRepository.registerForUserTimeChange(context)
        }
    }

    open fun onTrimMemory(level: Int) {
    }

    fun onConfigurationChanged() {
        systemTheme.update()
    }

    private fun initializeFirebase() {
        // If there is any issue while initializing Firebase then catch the Exception in release builds
        val intentionallyCrashFirebase = false
        try {
            if (intentionallyCrashFirebase) throw RuntimeException("Intentionally crashing Firebase initialization")
            FirebaseApp.initializeApp(context)
        } catch (ex: Exception) {
            if (buildConfig.debug && !intentionallyCrashFirebase) {
                throw ex
            } else {
                Log.e("Initializing FirebaseApp raised exception - intentional = %b", intentionallyCrashFirebase)
                Log.e(ex)
                crashTracking.logNonFatalException(ex)
            }
        }
    }

    fun init() {
        monitoringManager.initialize()
        cloudMessagingManager.initialize()
    }
}