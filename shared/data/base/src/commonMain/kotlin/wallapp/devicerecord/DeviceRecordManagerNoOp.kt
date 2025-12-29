package wallapp.devicerecord

object DeviceRecordManagerNoOp : DeviceRecordManager {

    override suspend fun getUpdatedDeviceRecords(fcmToken: String?): List<DeviceRecord>? {
        return null
    }
}