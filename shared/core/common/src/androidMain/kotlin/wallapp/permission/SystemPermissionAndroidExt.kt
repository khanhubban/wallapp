package wallapp.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import wallapp.permission.SystemPermissionMapperAndroid.defaultSystemPermissionStatus
import wallapp.permission.SystemPermissionMapperAndroid.mapToPermissions


fun Activity?.getPermissionStatus(systemPermissionType: SystemPermissionType): SystemPermissionStatus {
    if (!SystemPermissionMapperAndroid.requiresUserPermission(systemPermissionType)) {
        return systemPermissionType.defaultSystemPermissionStatus
    }

    val activity = this ?: return SystemPermissionStatus.Unknown

    val permissions = mapToPermissions(systemPermissionType)
    requireNotNull(permissions) { "Permissions must be defined for $systemPermissionType" }
    val allGranted = permissions.all {
        ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }
    return if (allGranted) SystemPermissionStatus.Authorized else SystemPermissionStatus.Denied
}
