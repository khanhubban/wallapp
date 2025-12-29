package wallapp.permission

import kotlinx.coroutines.flow.StateFlow

interface SystemPermissionManager {

    val systemMediaPermissionStatus: StateFlow<SystemPermissionStatus>

    val postNotificationPermissionStatus: StateFlow<SystemPermissionStatus>

    // Trigger a refresh of the permission status. Typically called manually when the app returns
    // to the foreground.
    fun refreshPermissionStatus()

    fun permissionGuardedAction(
        systemPermissionType: SystemPermissionType,
        actionOnSuccess: suspend () -> Unit,
        actionOnDenied: suspend () -> Unit
    )
}
