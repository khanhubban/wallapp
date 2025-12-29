package wallapp.appvisibility

import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import wallapp.log.Logger

class AppVisibilityIos : AppVisibility {

    companion object {
        val Log = Logger("AppVisibilityIos")
    }

    override val isVisible = MutableStateFlow(true)
    override val visible: Boolean
        get() = isVisible.value

    private fun updateVisibility(isVisible: Boolean) {
        if (this.isVisible.value == isVisible) return
        Log.d("updateVisibility() -> isVisible: ${this.isVisible.value} -> $isVisible")
        this.isVisible.value = isVisible
    }

    init {
        val notificationCenter = NSNotificationCenter.defaultCenter()

        notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue()
        ) { updateVisibility(true) }

        notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue()
        ) { updateVisibility(false) }
    }
}