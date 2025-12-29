package wallapp.extensions

import android.content.Intent
import android.os.Process
import android.os.UserHandle
import android.os.UserManager

/**
 * Long [android.content.Intent] extra that is used as serial number for [android.os.UserHandle]
 * resolved using [android.os.UserManager.getSerialNumberForUser]
 */
private const val USER_SERIAL_NUMBER = "user_serial_number"

fun Intent.getUserHandle(userManager: UserManager): UserHandle? {
    return if (hasExtra(USER_SERIAL_NUMBER)) {
        userManager.getUserForSerialNumber(getLongExtra(USER_SERIAL_NUMBER, 0L))
    } else {
        null
    }
}

@JvmOverloads
fun Intent.resolveUserHandle(userManager: UserManager,
                             defaultUserHandle: UserHandle = Process.myUserHandle()
)
        : UserHandle {
    return getUserHandle(userManager) ?: defaultUserHandle
}

fun Intent.setUserSerialNumber(serialNumber: Long) {
    putExtra(USER_SERIAL_NUMBER, serialNumber)
}

val Intent.debugString: String
    get() = buildString {
        append("Intent: Action: ${action}, Data: ${data}, Type: ${type}, Categories: ${categories}, ")
        val extras = extras
        if (extras != null) {
            append("extras:\n")
            for (key in extras.keySet()) {
                append(" Extra key: $key, value: ${extras.get(key)}\n")
            }
        } else {
            append("extras: none")
        }
    }
