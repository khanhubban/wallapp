package wallapp.permission

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyDenied
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale

fun List<PermissionStatus>.mapToSystemPermissionStatus(): SystemPermissionStatus {
    return when {
        allGranted() -> SystemPermissionStatus.Authorized
        anyPermanentlyDenied() -> SystemPermissionStatus.Restricted
        anyDenied() -> SystemPermissionStatus.Denied
        anyShouldShowRationale() -> SystemPermissionStatus.Limited
        else -> SystemPermissionStatus.Unknown
    }
}