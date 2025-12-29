package wallapp.system.ui.controller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.util.WeakReference

class UiControllerManagerAndroid : UiControllerManager {

    private var currentActivity: WeakReference<UiControllerAndroid>? = null
    override val currentUiController: UiControllerAndroid?
        get() = currentActivity?.get()
    private val _hasUiController = MutableStateFlow(false)
    val hasUiController: StateFlow<Boolean>
        get() = _hasUiController

    enum class LifecycleState {
        Created,
        Started,
//        Resumed,
//        Paused,
        Stopped,
        Destroyed,
    }

    private fun register(uiControllerAndroid: UiControllerAndroid, lifecycleState: LifecycleState) {
        if (lifecycleState == LifecycleState.Created
            || lifecycleState == LifecycleState.Started
        ) {
            if (currentUiController?.activity != uiControllerAndroid.activity) {
                currentActivity = WeakReference(uiControllerAndroid)
                _hasUiController.value = true
            }
        } else if (lifecycleState == LifecycleState.Destroyed) {
            if (currentUiController?.activity == uiControllerAndroid.activity) {
                currentActivity = null
                _hasUiController.value = false
            }
        }
    }

    fun onCreate(activity: UiControllerAndroid) {
        register(activity, LifecycleState.Created)
    }

    fun onDestroy(activity: UiControllerAndroid) {
        register(activity, LifecycleState.Destroyed)
    }

    fun onStop(activity: UiControllerAndroid) {
        register(activity, LifecycleState.Stopped)
    }

    fun onStart(activity: UiControllerAndroid) {
        register(activity, LifecycleState.Started)
    }
}