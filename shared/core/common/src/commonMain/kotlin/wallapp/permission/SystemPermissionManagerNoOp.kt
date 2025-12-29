package wallapp.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SystemPermissionManagerNoOp : SystemPermissionManager {

    override val systemMediaPermissionStatus: StateFlow<SystemPermissionStatus> = MutableStateFlow(SystemPermissionStatus.Unknown)
    override val postNotificationPermissionStatus: StateFlow<SystemPermissionStatus> = MutableStateFlow(SystemPermissionStatus.Unknown)

    override fun refreshPermissionStatus() { }

    override fun permissionGuardedAction(
        systemPermissionType: SystemPermissionType,
        actionOnSuccess: suspend () -> Unit,
        actionOnDenied: suspend () -> Unit,
    ) { }
}