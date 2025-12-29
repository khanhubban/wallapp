package wallapp.application

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import wallapp.appstate.AppState
import wallapp.appvisibility.AppVisibility
import wallapp.appvisibility.AppVisibilityDefaultProcess
import wallapp.di.Lazy
import wallapp.log.Log
import wallapp.process.Process
import wallapp.time.TimeRepository

class ApplicationLifecycleObserver(
    private val process: Lazy<Process>,
    appVisibility: AppVisibility,
    private val appStateLazy: Lazy<AppState>,
    private val timeRepository: Lazy<TimeRepository>,
): LifecycleObserver {

    private val appState: AppState by lazy { appStateLazy.get() }

    private val currentTime: Long
        get() = timeRepository.get().let { it.currentTimeVerified ?: it.currentTime }

    private val appVisibilityDefaultProcess: AppVisibilityDefaultProcess? by lazy {
        if (appVisibility is AppVisibilityDefaultProcess) {
            appVisibility
        } else {
            null
        }
    }

    private val processNameSuffix: String
        get() = process.get().processNameSuffix ?: ":default"

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d("[${processNameSuffix}] ApplicationObserver.onStart()")

        appVisibilityDefaultProcess?.updateVisibility(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.d("[${processNameSuffix}] ApplicationObserver.onStop()")

        appState.lastAppCloseTime.update(currentTime)
        appVisibilityDefaultProcess?.updateVisibility(false)
    }
}