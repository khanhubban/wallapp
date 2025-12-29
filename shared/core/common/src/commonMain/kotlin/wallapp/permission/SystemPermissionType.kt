package wallapp.permission

sealed class SystemPermissionType {

    data object SystemMedia : SystemPermissionType()

    data object PostNotifications : SystemPermissionType()
}