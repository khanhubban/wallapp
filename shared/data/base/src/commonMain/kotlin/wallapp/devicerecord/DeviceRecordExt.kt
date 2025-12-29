package wallapp.devicerecord

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wallapp.device.coarsenMajorMinor
import wallapp.log.Log

fun mapDeviceRecordsToExportString(deviceRecords: List<DeviceRecord>): String {
    if (deviceRecords.isEmpty()) return ""
    val normalizedRecords = deviceRecords.map { record ->
        record.copy(deviceInfo = record.deviceInfo.copy(osVersion = record.deviceInfo.osVersion.coarsenMajorMinor()))
    }
    return Json.encodeToString(normalizedRecords)
}

fun mapDeviceRecordsFromExportString(exportString: String): List<DeviceRecord> {
    if (exportString.isEmpty()) return emptyList()
    val json = Json { ignoreUnknownKeys = true }
    return try {
        json.decodeFromString(exportString)
    } catch (e: Exception) {
        Log.e("[DeviceRecords] Failed to decode device records export string: $e")
        emptyList()
    }
}

fun normalizeDeviceRecordsExportString(exportString: String): String? {
    if (exportString.isBlank()) return null
    val json = Json { ignoreUnknownKeys = true }
    val deviceRecords = runCatching { json.decodeFromString<List<DeviceRecord>>(exportString) }.getOrNull()
    if (deviceRecords == null) {
        Log.e("[DeviceRecords] Failed to normalize export string.")
        return null
    }
    if (deviceRecords.isEmpty()) return ""
    return mapDeviceRecordsToExportString(deviceRecords)
}
