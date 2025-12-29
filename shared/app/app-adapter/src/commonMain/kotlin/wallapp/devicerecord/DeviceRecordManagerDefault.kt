package wallapp.devicerecord

import wallapp.device.DeviceInfo
import wallapp.log.Log
import wallapp.result.dataOrNull
import wallapp.time.TimeRepository
import wallapp.userprofile.UserProfileRepository

class DeviceRecordManagerDefault(
    private val timeRepository: TimeRepository,
    private val currentDeviceInfo: DeviceInfo,
    private val userProfileRepository: UserProfileRepository,
) : DeviceRecordManager {

    // Current time in minutes to avoid multiple writes to the database
    private val currentTime: Long
        get() = timeRepository.currentTime / 1000 / 60

    private val currentDeviceRecord: DeviceRecord
        get() = DeviceRecord(currentDeviceInfo, currentTime)

    override suspend fun getUpdatedDeviceRecords(fcmToken: String?): List<DeviceRecord>? {
        val currentUserId = userProfileRepository.currentUserProfile.value.dataOrNull?.userId ?: return null
        // Get the current user profile from the server and not from cache as we are updating the entire
        // deviceInfo string and the local cache may not be up to date with other devices' changes
        val currentUserProfileFromWeb = userProfileRepository.getUserProfileFromServer(currentUserId) ?: return null
        val currentDeviceRecords = currentUserProfileFromWeb.deviceInfo?.let { mapDeviceRecordsFromExportString(it) }
        Log.d("[DeviceRecords] currentDeviceRecords: $currentDeviceRecords, newFcmToken: $fcmToken")
        val deviceRecordToAddOrUpdate = currentDeviceRecord.copy(fcmToken = fcmToken)
        val updatedDeviceRecords =  DeviceRecordArbitrator.update(
            existingRecords = currentDeviceRecords,
            deviceRecordToAddOrUpdate = deviceRecordToAddOrUpdate,
        )
        Log.d("[DeviceRecords] updatedDeviceRecords: $updatedDeviceRecords")
        return updatedDeviceRecords
    }
}
