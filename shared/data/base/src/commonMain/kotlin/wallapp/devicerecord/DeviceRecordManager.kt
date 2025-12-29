package wallapp.devicerecord

interface DeviceRecordManager {

    suspend fun getUpdatedDeviceRecords(fcmToken: String? = null): List<DeviceRecord>?
}
