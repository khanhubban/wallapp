package wallapp.devicerecord

import wallapp.device.OsVersionCompareResult
import wallapp.device.compareOsVersion

object DeviceRecordArbitrator {

    fun update(
        existingRecords: List<DeviceRecord>?,
        deviceRecordToAddOrUpdate: DeviceRecord?,
    ): List<DeviceRecord>? {
        val result = existingRecords?.toMutableList() ?: mutableListOf()

        fun updateLastUsedTime(index: Int, newLastUsedTime: Long) {
            result.apply {
                this[index] = this[index].copy(lastUsedTime = newLastUsedTime)
            }
        }

        fun updateFcmToken(index: Int, newFcmToken: String) {
            result.apply {
                this[index] = this[index].copy(fcmToken = newFcmToken)
            }
        }

        fun replaceRecord(index: Int, newRecord: DeviceRecord) {
            result.apply {
                this[index] = newRecord
            }
        }

        fun addNewRecord(newRecord: DeviceRecord) {
            result.add(newRecord)
        }

        val existingRecordIndex = existingRecords?.indexOfFirst {
            it.deviceInfo.deviceModelKey == deviceRecordToAddOrUpdate?.deviceInfo?.deviceModelKey &&
                    it.deviceInfo.osName == deviceRecordToAddOrUpdate.deviceInfo.osName
        } ?: -1

        if (existingRecordIndex >= 0) {
            requireNotNull(deviceRecordToAddOrUpdate)
            requireNotNull(existingRecords)
            val existingRecord = existingRecords[existingRecordIndex]
            val compareResult = compareOsVersion(
                deviceRecordToAddOrUpdate.deviceInfo.osVersion,
                existingRecord.deviceInfo.osVersion
            )

            when (compareResult) {
                OsVersionCompareResult.Equal -> {
                    updateLastUsedTime(existingRecordIndex, deviceRecordToAddOrUpdate.lastUsedTime)
                    if (deviceRecordToAddOrUpdate.fcmToken != null && existingRecord.fcmToken != deviceRecordToAddOrUpdate.fcmToken) {
                        updateFcmToken(existingRecordIndex, deviceRecordToAddOrUpdate.fcmToken)
                    }
                }

                OsVersionCompareResult.Newer,
                OsVersionCompareResult.Older,
                OsVersionCompareResult.Unknown
                -> {
                    // Different/unknown OS version; replace record.
                    replaceRecord(existingRecordIndex, deviceRecordToAddOrUpdate)
                }
            }

        } else if (deviceRecordToAddOrUpdate != null) {
            addNewRecord(deviceRecordToAddOrUpdate)
        }

        return result.ifEmpty { null }
    }
}