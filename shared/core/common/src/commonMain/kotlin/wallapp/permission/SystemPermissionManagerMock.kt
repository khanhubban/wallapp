package wallapp.permission

import kotlinx.coroutines.flow.MutableStateFlow

class SystemPermissionManagerMock(
    override val systemMediaPermissionStatus: MutableStateFlow<SystemPermissionStatus>,
    override val postNotificationPermissionStatus: MutableStateFlow<SystemPermissionStatus>
) : SystemPermissionManager {

    constructor(
        systemMediaPermissionStatus: SystemPermissionStatus = SystemPermissionStatus.Authorized,
        postNotificationPermissionStatus: SystemPermissionStatus = SystemPermissionStatus.Authorized
    ) : this(
        systemMediaPermissionStatus = MutableStateFlow(systemMediaPermissionStatus),
        postNotificationPermissionStatus = MutableStateFlow(postNotificationPermissionStatus),
    )

    constructor(permissionStatus: SystemPermissionStatus) : this(permissionStatus, permissionStatus)

    override fun refreshPermissionStatus() { }

    override fun permissionGuardedAction(
        systemPermissionType: SystemPermissionType,
        actionOnSuccess: suspend () -> Unit,
        actionOnDenied: suspend () -> Unit,
    ) { }
}

fun SystemPermissionManagerMockAuthorized() = SystemPermissionManagerMock(SystemPermissionStatus.Authorized)
fun SystemPermissionManagerMockDenied() = SystemPermissionManagerMock(SystemPermissionStatus.Denied)