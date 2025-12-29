package wallapp.inappbrowser

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import wallapp.activity.ActivityLifecycleListener
import wallapp.coroutine.CoroutineScopes
import wallapp.coroutine.collectIn
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.activity
import wallapp.util.WeakReference

private data class ConnectionRecord(
    val activity: WeakReference<Activity>,
    val connection: CustomTabsServiceConnection,
)

class InAppBrowserManagerAndroid(
    private val application: Context,
    private val uiControllerManager: UiControllerManager,
    private val coroutineScopes: CoroutineScopes,
) : InAppBrowserManager {

    companion object {
        val Log = InAppBrowserManagerLogger
    }

    override val isShowing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private fun Activity.openToSystemBrowser(
        uri: Uri,
        setActivityNewTask: Boolean,
    ) {
        Log.d("Opening to system browser: $uri")
        val intent = Intent(Intent.ACTION_VIEW, uri)
            .apply {
                if (setActivityNewTask) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
        startActivity(this, intent, null)
    }

    override fun show(url: String): Flow<InAppBrowserEvent> {
        val setActivityNewTask = true
        val inAppBrowserEvents = MutableSharedFlow<InAppBrowserEvent>(replay = 0)
        isShowing.value = false

        fun emitEvent(event: InAppBrowserEvent) {
            Log.d("$event")
            coroutineScopes.main.launch {
                if (event == InAppBrowserEvent.Shown) {
                    isShowing.value = true
                } else if (event == InAppBrowserEvent.Dismissed) {
                    isShowing.value = false
                }
                inAppBrowserEvents.emit(event)
            }
        }

        val activity = uiControllerManager.currentUiController?.activity
            ?: run {
                emitEvent(InAppBrowserEvent.Error)
                Log.e(" No activity found to show in-app browser")
                return inAppBrowserEvents
            }

        val uri = Uri.parse(url)

        val packageName = CustomTabsClient.getPackageName(application, null)
        if (packageName == null) {
            activity.openToSystemBrowser(uri, setActivityNewTask)
            emitEvent(InAppBrowserEvent.OpenedToSystemBrowser)
            return inAppBrowserEvents
        }

        val customTabsCallback = object : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                when (navigationEvent) {
                    CustomTabsCallback.NAVIGATION_STARTED -> {
                        Log.d("NAVIGATION_STARTED")
//                        emitEvent(InAppBrowserEvent.Loading)
                    }
                    CustomTabsCallback.TAB_SHOWN -> {
                        Log.d("TAB_SHOWN")
                        emitEvent(InAppBrowserEvent.Shown)
                    }
                    CustomTabsCallback.TAB_HIDDEN -> {
                        Log.d("TAB_HIDDEN")
                        emitEvent(InAppBrowserEvent.Dismissed)
                    }
                }
            }
        }

        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                val session = client.newSession(customTabsCallback)
                if (session == null) {
                    Log.e("Failed to create session, falling back to system browser")
                    activity.openToSystemBrowser(uri, setActivityNewTask)
                    emitEvent(InAppBrowserEvent.OpenedToSystemBrowser)
                    return
                }

                val customTabsIntent = CustomTabsIntent.Builder(session)
                    .setShowTitle(true)
                    .build()

                // Launch the Custom Tab with the session
                customTabsIntent
                    .apply {
                        if (setActivityNewTask) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    }
                    .launchUrl(activity, uri)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                emitEvent(InAppBrowserEvent.Dismissed)
                cleanupForActivity(activity)
            }
        }

        cleanupForActivity(activity)

        if (CustomTabsClient.bindCustomTabsService(activity, packageName, connection)) {
            connectionRecords.add(ConnectionRecord(WeakReference(activity), connection))
        } else {
            activity.openToSystemBrowser(uri, setActivityNewTask)
            emitEvent(InAppBrowserEvent.OpenedToSystemBrowser)
        }

        return inAppBrowserEvents
    }

    private val connectionRecords: MutableList<ConnectionRecord> = mutableListOf()

    private fun cleanupForActivity(activity: Activity) {
        val iterator = connectionRecords.iterator()
        while (iterator.hasNext()) {
            val record = iterator.next()
            val itrActivity = record.activity.get()
            if (itrActivity == null || itrActivity == activity) {
                try {
                    application.unbindService(record.connection)
                } catch (_: IllegalArgumentException) { }
                iterator.remove()
            }
        }
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleListener.Callbacks() {

        override fun onActivityDestroyed(activity: Activity) {
            cleanupForActivity(activity)
        }
    }

    init {
        ActivityLifecycleListener(application as Application, activityLifecycleCallbacks)

        isShowing.collectIn(coroutineScopes.main) {
            Log.i("isShowing: $it")
        }
    }
}
