package wallapp.monitoring

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.appconfig.AppConfig
import wallapp.buildconfig.BuildConfig
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.CrashTrackingLocal
import wallapp.di.Lazy
import wallapp.log.Log
import wallapp.preferences.UserPreferences

class MonitoringManagerDefault(
    private val buildConfig: BuildConfig,
    private val appConfig: AppConfig,
    crashTrackingLocal: Lazy<CrashTracking>,
    crashTrackingRemote: Lazy<CrashTracking>,
    userPreferences: Lazy<UserPreferences>,
    private val coroutineScopeMain: CoroutineScope,
) : MonitoringManager {

    private val userPreferences by lazy { userPreferences.get() }
    private val reportUsageStats: StateFlow<Boolean>
        get() = userPreferences.reportUsageStats

    private val crashTrackingLocal by lazy {
        crashTrackingLocal.get().also {
            require(it is CrashTrackingLocal) { "crashTrackingLocal must be CrashTrackingLocal" }
        }
    }
    private val crashTrackingRemote by lazy { crashTrackingRemote.get() }

    private fun initializeCrashTracking() {
        CrashTrackingHolder.set(
            if (reportUsageStats.value) { crashTrackingRemote } else { crashTrackingLocal }
        )

        coroutineScopeMain.launch {
            reportUsageStats.collect { reportUsageStats ->
                CrashTrackingHolder.set(
                    if (reportUsageStats) {
                        crashTrackingRemote
                    } else {
                        crashTrackingLocal
                    }
                )
            }
        }
    }

    private fun initializeLogging() {
        Log.setDisabled(disabled = !buildConfig.debug)
        coroutineScopeMain.launch {
            appConfig.enableLogging.collect {
                Log.setDisabled(disabled = !it)
            }
        }
    }

    override fun initialize() {
        initializeLogging()
        initializeCrashTracking()
    }
}