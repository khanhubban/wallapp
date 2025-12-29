package wallapp.permission

import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional

object SystemPermissionMapperIos {

    fun toSystemPermissionStatusUn(status: UNAuthorizationStatus): SystemPermissionStatus =
        when (status) {
            UNAuthorizationStatusAuthorized -> SystemPermissionStatus.Authorized
            UNAuthorizationStatusDenied -> SystemPermissionStatus.Denied
            UNAuthorizationStatusEphemeral -> SystemPermissionStatus.Ephemeral
            UNAuthorizationStatusNotDetermined -> SystemPermissionStatus.NotDetermined
            UNAuthorizationStatusProvisional -> SystemPermissionStatus.Provisional
            else -> SystemPermissionStatus.Unknown // Fallback for unknown cases
        }

    fun toSystemPermissionStatusPh(status: PHAuthorizationStatus): SystemPermissionStatus =
        when (status) {
            PHAuthorizationStatusAuthorized -> SystemPermissionStatus.Authorized
            PHAuthorizationStatusDenied -> SystemPermissionStatus.Denied
            PHAuthorizationStatusLimited -> SystemPermissionStatus.Limited
            PHAuthorizationStatusNotDetermined -> SystemPermissionStatus.NotDetermined
            PHAuthorizationStatusRestricted -> SystemPermissionStatus.Restricted
            else -> SystemPermissionStatus.Unknown // Fallback for unknown cases
        }
}