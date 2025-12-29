package wallapp.devicerecord

import kotlinx.coroutines.test.runTest
import wallapp.device.DeviceInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceRecordArbitratorTest {

    @Test
    fun `addDeviceRecord adds a new device record`() = runTest {
        val newDeviceRecord = DeviceRecord(DeviceInfo("Manufacturer", "ModelName", "OSName", "OSVersion"), 1000L)

        val records = DeviceRecordArbitrator.update(existingRecords = null, newDeviceRecord)
        assertEquals(1, records?.size)
        assertEquals(newDeviceRecord, records?.get(0))
    }

    @Test
    fun `addDeviceRecord updates last used time for existing record with same OS version`() = runTest {
        val initialDeviceRecord = DeviceRecord(DeviceInfo("Manufacturer", "ModelName", "OSName", "1.0"), 1000L)

        var records = DeviceRecordArbitrator.update(existingRecords = null, initialDeviceRecord)

        val updatedDeviceRecord = initialDeviceRecord.copy(lastUsedTime = 2000L)
        records =DeviceRecordArbitrator.update(records, updatedDeviceRecord)

        assertEquals(1, records?.size)
        assertEquals(2000L, records?.get(0)?.lastUsedTime)
    }

    @Test
    fun `addDeviceRecord replaces existing record with different OS version`() = runTest {
        val initialDeviceRecord = DeviceRecord(DeviceInfo("Manufacturer", "ModelName", "OSName", "1.0"), 1000L)
        var records = DeviceRecordArbitrator.update(existingRecords = null, initialDeviceRecord)

        val newVersionRecord = DeviceRecord(DeviceInfo("Manufacturer", "ModelName", "OSName", "2.0"), 1500L)
        records = DeviceRecordArbitrator.update(existingRecords = records, newVersionRecord)

        assertEquals(1, records?.size)
        assertEquals("2.0", records?.get(0)?.deviceInfo?.osVersion)
    }

    @Test
    fun `addDeviceRecord retains multiple distinct device records`() = runTest {
        val iPadRecord = DeviceRecord(DeviceInfo("Apple", "iPad", "iOS", "14.0"), 1000L)
        val iPhoneRecord = DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "14.2"), 1100L)
        val androidRecord = DeviceRecord(DeviceInfo("Google", "Pixel", "Android", "11"), 1200L)

        var records = DeviceRecordArbitrator.update(existingRecords = null, iPadRecord)
        records = DeviceRecordArbitrator.update(records, iPhoneRecord)
        records = DeviceRecordArbitrator.update(records, androidRecord)

        assertEquals(3, records?.size)
        assertTrue(records?.containsAll(listOf(iPadRecord, iPhoneRecord, androidRecord)) == true)
    }

    @Test
    fun `updateDeviceRecord updates only the specified record among multiple`() = runTest {
        val initialRecords = listOf(
            DeviceRecord(DeviceInfo("Apple", "iPad", "iOS", "14.0"), 1000L),
            DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "14.2"), 1100L),
            DeviceRecord(DeviceInfo("Google", "Pixel", "Android", "11"), 1200L)
        )

        val updatedRecord = DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "14.2"), 2000L)
        val records = DeviceRecordArbitrator.update(
            existingRecords = initialRecords,
            deviceRecordToAddOrUpdate = updatedRecord,
        )

        assertEquals(3, records?.size)
        assertTrue(records?.any { it == updatedRecord && it.lastUsedTime == 2000L } == true)
    }

    @Test
    fun `addDeviceRecord replaces device record with newer OS version among multiple devices`() = runTest {
        val initialRecords = listOf(
            DeviceRecord(DeviceInfo("Apple", "iPad", "iOS", "14.0"), 1000L),
            DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "14.2"), 1100L)
        )
//        initialRecords.forEach { deviceRecordManagerDefault.addDeviceRecord(it) }

        val newOSVersioniPhoneRecord = DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "15.0"), 2100L)
//        deviceRecordManagerDefault.addDeviceRecord(newOSVersioniPhoneRecord)

        val records = DeviceRecordArbitrator.update(
            existingRecords = initialRecords,
            deviceRecordToAddOrUpdate = newOSVersioniPhoneRecord,
        )

        assertEquals(2, records?.size)
        assertTrue(records?.any { it.deviceInfo.osVersion == "15.0" && it.lastUsedTime == 2100L } == true)
    }

    @Test
    fun `updateDeviceRecord replaces iPhone record with different minor OS version`() = runTest {
        val initialRecords = listOf(
            DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "17.0.1"), 1000L),
        )

        val updatedRecord = DeviceRecord(DeviceInfo("Apple", "iPhone", "iOS", "17.5.1"), 2000L)
        val records = DeviceRecordArbitrator.update(
            existingRecords = initialRecords,
            deviceRecordToAddOrUpdate = updatedRecord,
        )

        assertEquals(1, records?.size)
    }

}
