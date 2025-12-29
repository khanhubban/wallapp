package wallapp.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    @SerialName("oem")
    val manufacturer: String,
    @SerialName("model")
    val modelName: String,
    @SerialName("os")
    val osName: String,
    val osVersion: OsVersion,
) {
    constructor(
        deviceModel: DeviceModel,
        osName: String,
        osVersion: OsVersion,
    ) : this(
        manufacturer = deviceModel.manufacturer,
        modelName = deviceModel.modelName,
        osName = osName,
        osVersion = osVersion,
    )

    val deviceModel: DeviceModel
        get() = DeviceModel(manufacturer, modelName)

    val deviceModelKey: String by lazy {
        "${manufacturer.lowercase()}_${modelName.lowercase()}"
            .replace("\\s".toRegex(), "")
    }

    val isEmulator: Boolean
        get() = isAndroidEmulator || isIosEmulator()

    private val isAndroidEmulator: Boolean
        get() = modelName.contains("sdk_google")
                || modelName.contains("google_sdk")
                || modelName.contains("sdk")
                || modelName.contains("sdk_x86")
                || modelName.contains("sdk_gphone64_arm64")

    companion object {

        val Preset = DeviceInfo(
            manufacturer = "Preset~Manufacturer",
            modelName = "Preset~ModelName",
            osName = "Preset~OS",
            osVersion = "Preset~Version",
        )
    }
}

expect fun createSystemDeviceInfo(): DeviceInfo
expect fun isIosEmulator(): Boolean
