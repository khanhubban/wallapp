package wallapp.permission

import android.Manifest
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object SystemPermissionMapperAndroid {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    private val systemMediaPermissionAutoGranted: Boolean = Build.VERSION.SDK_INT >= 29
    private val systemMediaPermissions: List<String>? = if (systemMediaPermissionAutoGranted) {
        // https://developer.android.com/training/data-storage/shared/media#storage-permission-not-always-needed
        null
    } else {
        listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    private val postNotificationPermissionAutoGranted = Build.VERSION.SDK_INT < 33
    private val postNotificationPermissions: List<String>? = if (!postNotificationPermissionAutoGranted) {
        listOf(
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        null
    }

    private val systemPermissionsMap: Map<SystemPermissionType, List<String>?> = mapOf(
        SystemPermissionType.SystemMedia to systemMediaPermissions,
        SystemPermissionType.PostNotifications to postNotificationPermissions,
    )

    fun mapToPermissions(systemPermissionType: SystemPermissionType): List<String>? {
        return systemPermissionsMap[systemPermissionType]!!
    }

    fun requiresUserPermission(systemPermissionType: SystemPermissionType): Boolean {
        return systemPermissionsMap[systemPermissionType] != null
    }

    val SystemPermissionType.defaultSystemPermissionStatus: SystemPermissionStatus
        get() = when (this) {
            SystemPermissionType.PostNotifications -> if (postNotificationPermissionAutoGranted) {
                SystemPermissionStatus.Authorized
            } else {
                SystemPermissionStatus.Unknown
            }
            SystemPermissionType.SystemMedia -> if (systemMediaPermissionAutoGranted) {
                SystemPermissionStatus.Authorized
            } else {
                SystemPermissionStatus.Unknown
            }
        }
}