package wallapp.devicerecord

import kotlinx.coroutines.test.runTest
import wallapp.device.DeviceInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceRecordExtKtTest {

    @Test
    fun `mapDeviceRecordsToExportString serializes non-empty list correctly`() = runTest {
        val deviceRecords = listOf(
            DeviceRecord(
                deviceInfo = DeviceInfo(
                    manufacturer = "Samsung",
                    modelName = "Galaxy S10",
                    osName = "Android",
                    osVersion = "10"
                ),
                lastUsedTime = 1622505600000L
            ),
            DeviceRecord(
                deviceInfo = DeviceInfo(
                    manufacturer = "Apple",
                    modelName = "iPhone 12",
                    osName = "iOS",
                    osVersion = "14.4.2"
                ),
                lastUsedTime = 1625087600000L
            )
        )

        val exportString = mapDeviceRecordsToExportString(deviceRecords)
        assertTrue(exportString.contains("Samsung") && exportString.contains("Apple"))
    }

    @Test
    fun `mapDeviceRecordsFromExportString deserializes non-empty list correctly`() = runTest {
        val exportString = """
            [
                {
                    "device": {
                        "oem": "Samsung",
                        "model": "Galaxy S10",
                        "os": "Android",
                        "osVersion": "10"
                    },
                    "lastUsedTime": 1622505600000
                },
                {
                    "device": {
                        "oem": "Apple",
                        "model": "iPhone 12",
                        "os": "iOS",
                        "osVersion": "14.4.2"
                    },
                    "lastUsedTime": 1625087600000
                }
            ]
        """.trimIndent()

        val deviceRecords = mapDeviceRecordsFromExportString(exportString)
        assertEquals(2, deviceRecords.size)
        assertEquals("Samsung", deviceRecords[0].deviceInfo.manufacturer)
        assertEquals("14.4.2", deviceRecords[1].deviceInfo.osVersion)
    }

    @Test
    fun `serialization and deserialization are reversible for a device record`() = runTest {
        val originalRecord = listOf(
            DeviceRecord(
                deviceInfo = DeviceInfo(
                    manufacturer = "OnePlus",
                    modelName = "9 Pro",
                    osName = "Android",
                    osVersion = "11"
                ),
                lastUsedTime = 1627699200000L
            )
        )

        val exportString = mapDeviceRecordsToExportString(originalRecord)
        val deserializedRecords = mapDeviceRecordsFromExportString(exportString)

        assertEquals(originalRecord.size, deserializedRecords.size)
        assertEquals(originalRecord[0].deviceInfo.manufacturer, deserializedRecords[0].deviceInfo.manufacturer)
        assertEquals(originalRecord[0].deviceInfo.modelName, deserializedRecords[0].deviceInfo.modelName)
        assertEquals(originalRecord[0].deviceInfo.osName, deserializedRecords[0].deviceInfo.osName)
        assertEquals(originalRecord[0].deviceInfo.osVersion, deserializedRecords[0].deviceInfo.osVersion)
        assertEquals(originalRecord[0].lastUsedTime, deserializedRecords[0].lastUsedTime)
    }

    @Test
    fun `mapDeviceRecordsToExportString handles empty list`() = runTest {
        val deviceRecords = emptyList<DeviceRecord>()
        val exportString = mapDeviceRecordsToExportString(deviceRecords)
        assertEquals("", exportString.trim())
    }

    @Test
    fun `mapDeviceRecordsFromExportString handles empty string`() = runTest {
        assertTrue(mapDeviceRecordsFromExportString("[]").isEmpty())
        assertTrue(mapDeviceRecordsFromExportString("").isEmpty())

    }
}
