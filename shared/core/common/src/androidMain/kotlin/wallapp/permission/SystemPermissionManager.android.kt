package wallapp.permission

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.fondesa.kpermissions.coroutines.flow
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import wallapp.activity.ActivityLifecycleListener
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.permission.SystemPermissionMapperAndroid.defaultSystemPermissionStatus
import wallapp.permission.SystemPermissionMapperAndroid.requiresUserPermission
import wallapp.system.ui.controller.UiControllerManagerAndroid
import wallapp.system.ui.controller.currentActivity

class SystemPermissionManagerAndroid(
    context: Context,
    private val uiControllerManager: UiControllerManagerAndroid,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : SystemPermissionManager {

    companion object {
        val Log = Logger("SystemPermissionManagerAndroid")
    }

    private val currentActivity: FragmentActivity?
        get() = uiControllerManager.currentActivity as FragmentActivity?

    override val systemMediaPermissionStatus: MutableStateFlow<SystemPermissionStatus> =
        MutableStateFlow(SystemPermissionType.SystemMedia.defaultSystemPermissionStatus)
    override val postNotificationPermissionStatus: MutableStateFlow<SystemPermissionStatus> =
        MutableStateFlow(SystemPermissionType.PostNotifications.defaultSystemPermissionStatus)

    private val SystemPermissionType.stateFlow: MutableStateFlow<SystemPermissionStatus>
        get() = when (this) {
            is SystemPermissionType.SystemMedia -> systemMediaPermissionStatus
            is SystemPermissionType.PostNotifications -> postNotificationPermissionStatus
        }

    private fun requestPermission(systemPermissionType: SystemPermissionType): Flow<SystemPermissionStatus> {
        Log.d("Requesting permission: $systemPermissionType")
        if (requiresUserPermission(systemPermissionType)) {
            systemPermissionType.stateFlow.value = SystemPermissionStatus.Requested
            val activity: FragmentActivity? = currentActivity
            val permissions = SystemPermissionMapperAndroid.mapToPermissions(systemPermissionType)
            if (activity != null && permissions != null) {
                val request = activity.permissionsBuilder(permissions).build()
                activity.lifecycleScope.launch {
                    request.flow()
                        .map { it.mapToSystemPermissionStatus() }
                        .collect {
                            Log.d("Post-permission request status: $it")
                            systemPermissionType.stateFlow.value = it
                        }
                }
                request.send()
            }
        } else {
            systemPermissionType.stateFlow.value = SystemPermissionStatus.Authorized
        }

        return systemPermissionType.stateFlow
    }

    override fun refreshPermissionStatus() {
        Log.d("Refreshing all permission statuses")
        coroutineScopeIo.launch {
            refreshSystemMediaPermissionStatus()
            refreshPostNotificationPermissionStatus()
        }
    }

    private fun getPermissionStatus(systemPermissionType: SystemPermissionType): SystemPermissionStatus {
        return currentActivity.getPermissionStatus(systemPermissionType)
    }

    private fun refreshSystemMediaPermissionStatus() {
        systemMediaPermissionStatus.value = getPermissionStatus(SystemPermissionType.SystemMedia)
    }

    private fun refreshPostNotificationPermissionStatus() {
        postNotificationPermissionStatus.value = getPermissionStatus(SystemPermissionType.PostNotifications)
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleListener.Callbacks() {

        override fun onActivityPostStarted(activity: Activity) {
            refreshPermissionStatus()
        }
    }

    override fun permissionGuardedAction(
        systemPermissionType: SystemPermissionType,
        actionOnSuccess: suspend () -> Unit,
        actionOnDenied: suspend () -> Unit
    ) {
        coroutineScopeMain.launch {
            val permissionStatus = systemPermissionType.stateFlow.value
            if (permissionStatus.isAuthorizedOrLimited()) {
                actionOnSuccess()
            } else {
                val updatedPermission = requestPermission(systemPermissionType)
                    .firstOrNull {
                        it is SystemPermissionStatus.Authorized ||
                        it is SystemPermissionStatus.Restricted ||
                        it is SystemPermissionStatus.Denied
                    }
                updatedPermission?.let {
                    when (it) {
                        is SystemPermissionStatus.Authorized -> actionOnSuccess()
                        else -> actionOnDenied()
                    }
                }
            }
        }
    }

    init {
        Log.d("Init")

        refreshPermissionStatus()

        ActivityLifecycleListener(context as Application, activityLifecycleCallbacks)

        systemMediaPermissionStatus.collectIn(coroutineScopeIo) {
            Log.d("System media permission status: $it")
        }
        postNotificationPermissionStatus.collectIn(coroutineScopeIo) {
            Log.d("Post notification permission status: $it")
        }
    }
}
