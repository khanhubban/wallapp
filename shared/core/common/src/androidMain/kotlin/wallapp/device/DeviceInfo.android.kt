package wallapp.device

import android.os.Build

actual fun isIosEmulator(): Boolean = false

actual fun createSystemDeviceInfo(): DeviceInfo {
    val deviceModel = DeviceModel(Build.MANUFACTURER, Build.MODEL)
    return DeviceInfo(
        deviceModel = deviceModel,
        osName = "Android",
        osVersion = Build.VERSION.RELEASE,
    )
}
