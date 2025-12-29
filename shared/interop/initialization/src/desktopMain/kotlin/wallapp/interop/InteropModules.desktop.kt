package wallapp.interop

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import wallapp.account.AccountManager
import wallapp.application.Application
import wallapp.image.loader.ImageLoader
import wallapp.lifecycle.AppLifecycleManager
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshManager
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.system.window.WindowFrameManager
import wallapp.system.window.WindowFrameManagerDefault

object InteropModulesDesktop: KoinComponent {

    val application: Application = get()

    val appLifecycleManager: AppLifecycleManager
        get() = get()
    val accountManager: AccountManager
        get() = get()
    val imageLoader: ImageLoader = get()
    val networkErrorBroadcaster: NetworkErrorBroadcaster
        get() = get()
    val networkRefreshManager: NetworkRefreshManager
        get() = get()
    val networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster
        get() = get()
    val windowFrameManagerDefault: WindowFrameManagerDefault
        get() = get<WindowFrameManager>() as WindowFrameManagerDefault
}
