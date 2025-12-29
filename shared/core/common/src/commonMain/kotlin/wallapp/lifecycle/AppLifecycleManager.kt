package wallapp.lifecycle

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AppLifecycleManager {

    val isAppInForeground: StateFlow<Boolean>

    fun appCameToForeground()

    fun appWentToBackground()
}

object AppLifecycleManagerNoOp : AppLifecycleManager {

    override val isAppInForeground: StateFlow<Boolean> = TODO()

    override fun appCameToForeground() {
        // No-op
    }

    override fun appWentToBackground() {
        // No-op
    }
}

class AppLifecycleManagerDefault : AppLifecycleManager {

    private val _isAppInForeground = MutableStateFlow(false)
    override val isAppInForeground: StateFlow<Boolean>
        get() = _isAppInForeground.asStateFlow()

    override fun appCameToForeground() {
        _isAppInForeground.value = true
    }

    override fun appWentToBackground() {
        _isAppInForeground.value = false
    }
}