package wallapp.permission

sealed class SystemPermissionStatus {

    data object Unknown : SystemPermissionStatus()

    data object Authorized : SystemPermissionStatus()

    data object Denied : SystemPermissionStatus()

    /**
     * iOS: the app is authorized to schedule or receive notifications for a limited amount of time.
     */
    data object Ephemeral : SystemPermissionStatus()

    /**
     * iOS: The user authorized this app for limited photo library access.
     */
    data object Limited : SystemPermissionStatus()

    /**
     * The user hasn’t yet made a choice about whether the app has permission.
     */
    data object NotDetermined : SystemPermissionStatus()

    /**
     * iOS: The application is provisionally authorized to post noninterruptive user notifications.
     */
    data object Provisional : SystemPermissionStatus()

    data object Requested : SystemPermissionStatus()

    /**
     * Used on iOS if the app is not authorized to use the service, and the user cannot
     * grant this permission directly. This is common in parental control scenarios.
     */
    data object Restricted : SystemPermissionStatus()

    val name: String
        get() = when (this) {
            is Authorized -> "Authorized"
            is Denied -> "Denied"
            is Ephemeral -> "Ephemeral"
            is Limited -> "Provisional"
            is NotDetermined -> "NotDetermined"
            is Provisional -> "Provisional"
            is Requested -> "Requested"
            is Restricted -> "Restricted"
            is Unknown -> "Unknown"
        }
}

fun SystemPermissionStatus.isAuthorizedOrLimited(): Boolean {
    return this is SystemPermissionStatus.Authorized || this is SystemPermissionStatus.Limited
}