package wallapp.device

import platform.UIKit.UIDevice

actual fun isIosEmulator(): Boolean {
    return UIDevice.currentDevice.model.contains("Simulator")
}

actual fun createSystemDeviceInfo(): DeviceInfo {
    val device = UIDevice.currentDevice
    val deviceModel = DeviceModel(
        manufacturer = "Apple",
        modelName = device.model,
    )

    return DeviceInfo(
        deviceModel = deviceModel,
        osName = device.systemName,
        osVersion = device.systemVersion,
    )
}
