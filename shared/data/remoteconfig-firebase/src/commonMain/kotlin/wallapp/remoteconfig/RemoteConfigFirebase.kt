package wallapp.remoteconfig

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import wallapp.buildconfig.BuildConfig
import wallapp.device.state.DeviceState
import wallapp.log.Log
import wallapp.remoteconfig.data.RemoteConfigEntry
import wallapp.util.assertNotMainThread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RemoteConfigFirebase(
    private val buildConfig: BuildConfig,
    private val coroutineScopeIo: CoroutineScope,
    deviceState: DeviceState,
) : RemoteConfig {

    private val remoteConfig by lazy { Firebase.remoteConfig }

    private val isDeveloperMode: Boolean
        get() = buildConfig.debug
    private val cacheExpiration: Duration by lazy {
        if (isDeveloperMode) {
            0.seconds
        } else {
            RemoteConfig.ConfigCacheExpiration
        }
    }

    private val _dataRefreshed = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val dataRefreshed: SharedFlow<Unit>
        get() = _dataRefreshed

    init {
        if (deviceState.isUserUnlocked) {
            tryInit()
        } else {
            deviceState.registerForUserUnlock {
                tryInit()
            }
        }
    }

    private fun tryInit() {
        coroutineScopeIo.launch {
            try {
                initFirebaseRemoteConfig()
            } catch (e: Exception) {
                Log.e("Failed to initialize FirebaseRemoteConfig", e)
            }
        }
    }

    private suspend fun initFirebaseRemoteConfig() {
        assertNotMainThread { Log.e("[RemoteConfig] Must not create on main thread") }

        remoteConfig.apply {
            settings {
                if (isDeveloperMode) {
                    minimumFetchIntervalInSeconds = 0
                }
            }
            setDefaults(*RemoteConfigEntry.asDefaultsArray())
        }
        _dataRefreshed.emit(Unit)

        update()
        addOnConfigUpdateListener { updated ->
            Log.d("[RemoteConfig] FirebaseRemoteConfig updated: $updated")
            if (updated) {
                activateAndEmit()
            }
        }
    }

    override suspend fun update() {
        try {
            Log.d("[RemoteConfig] Fetching FirebaseRemoteConfig...")
            remoteConfig.fetchAndActivate()
            _dataRefreshed.emit(Unit)
            Log.d("[RemoteConfig] FirebaseRemoteConfig updated")
        } catch (e: Exception) {
            Log.e("[RemoteConfig] Failed to update FirebaseRemoteConfig", e)
        }
    }

    private fun activateAndEmit() {
        coroutineScopeIo.launch {
            try {
                remoteConfig.activate()
                _dataRefreshed.tryEmit(Unit)
            } catch (e: Exception) {
                Log.e("[RemoteConfig] Failed to activate FirebaseRemoteConfig", e)
            }
        }
    }
}

expect fun addOnConfigUpdateListener(listener: (Boolean) -> Unit)