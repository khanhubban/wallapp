package wallapp.system.notificationcenter

import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue

/**
 * Somewhat of an equivalent for Android's [ActivityLifecycleListener].
 */
object NotificationCenterIos {

    /**
     * Adds a block to be executed when the app enters the foreground. Note: API doesn't currently
     * support removing the observer, so should only be used in a singleton or similar.
     */
    fun addWillEnterForegroundNotification(block: () -> Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter()
        notificationCenter.addObserverForName(
            name = "UIApplicationWillEnterForegroundNotification",
            `object` = null,
            queue = NSOperationQueue.mainQueue(),
        ) {
            block()
        }
    }

}