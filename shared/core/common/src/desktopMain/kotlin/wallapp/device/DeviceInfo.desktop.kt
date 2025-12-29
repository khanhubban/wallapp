package wallapp.device

actual fun isIosEmulator(): Boolean = false

actual fun createSystemDeviceInfo(): DeviceInfo {
    val deviceModel = DeviceModel(
        manufacturer = "Unknown",
        modelName = "Computer"
    )
    return DeviceInfo(
        deviceModel = deviceModel,
        osName = "Unknown",
        osVersion = "Unknown"
    )
}
