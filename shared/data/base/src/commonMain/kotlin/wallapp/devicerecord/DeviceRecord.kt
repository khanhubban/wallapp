package wallapp.devicerecord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wallapp.device.DeviceInfo

@Serializable
data class DeviceRecord(
    // Stored per user to distinguish devices so FCM tokens can be reconciled across multiple devices.
    @SerialName("device")
    val deviceInfo: DeviceInfo,
    val lastUsedTime: Long, // Time is in minutes since epoch
    val fcmToken: String? = null,
)
