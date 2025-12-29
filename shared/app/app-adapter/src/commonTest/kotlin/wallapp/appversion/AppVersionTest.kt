package wallapp.appversion

import wallapp.appversion.AppVersion.AppVersionAndroid
import wallapp.appversion.AppVersion.AppVersionIos
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppVersionTest {

    @Test
    fun `log iOS`() {
        println(AppVersionIos("1.0", 1).jsonString)
    }

    @Test
    fun `fromJson Ios`() {
        val json = """{"type":"AppVersionIos","data":{"shortVersion":"1.0","buildNumber":1}}"""
        val appVersion = AppVersion.fromExportString(json)
        assertEquals(AppVersionIos("1.0", 1), appVersion)
    }

    @Test
    fun `log Android`() {
        println(AppVersionAndroid(versionName = "1.2.3", 1230).jsonString)
    }

    @Test
    fun `fromJson Android`() {
        val json = """{"type":"AppVersionAndroid","data":{"versionName":"1.2.3","versionCode":1230}}"""
        val appVersion = AppVersion.fromExportString(json)
        assertEquals(AppVersionAndroid("1.2.3", 1230), appVersion)
    }

    @Test
    fun `from invalid Json`() {
        assertNull(AppVersion.fromExportString(""))
        assertNull(AppVersion.fromExportString("ios"))
        assertNull(AppVersion.fromExportString("{\"type\":\"wallapp.appversion.AppVersion.AppVersionIos\",\"shortVersion\":\"1a.0\",\"buildNumber\":1}\n"))
    }

    @Test
    fun `is valid`() {
        assertFalse(AppVersionIos("dfs", 1).isValid())
        assertFalse(AppVersionIos("1.0", 0).isValid())
        assertFalse(AppVersionAndroid("unused", 0).isValid())
    }

    @Test
    fun `to and from Json - iOS`() {
        val appVersion = AppVersionIos("1.0", 1)
        val json = appVersion.jsonString
        val fromJson = AppVersion.fromExportString(json)
        assertEquals(appVersion, fromJson)
    }

    @Test
    fun `iOS versionName`() {
        val appVersion = AppVersionIos("1.0", 3)
        assertEquals("1.0 (3)", appVersion.versionName)
    }

    @Test
    fun `to and from Json - Android`() {
        val appVersion = AppVersionAndroid("1.0.0", 1)
        val json = appVersion.jsonString
        val fromJson = AppVersion.fromExportString(json)
        assertEquals(appVersion, fromJson)
    }

    @Test
    fun `isNewerThan Android versionCode`() {
        val newer = AppVersionAndroid("unused1", 2)
        val older = AppVersionAndroid("unused2", 1)
        assertTrue(newer.isNewerThan(older))
    }

    @Test
    fun `isNewerThan iOS buildNumber`() {
        val newer = AppVersionIos("1.1.0", 2)
        val older = AppVersionIos("1.1.0", 1)
        assertTrue(newer.isNewerThan(older) == true)
    }

    @Test
    fun `isNewerThan iOS patch`() {
        val newer = AppVersionIos("1.1.10", 1)
        val older = AppVersionIos("1.1.9", 2)
        assertTrue(newer.isNewerThan(older) == true)
    }

    @Test
    fun `isNewerThan iOS minor`() {
        val newer = AppVersionIos("1.10.0", 1)
        val older = AppVersionIos("1.9.99", 2)
        assertTrue(newer.isNewerThan(older) == true)
    }

    @Test
    fun `isNewerThan iOS major`() {
        val newer = AppVersionIos("10.0.1", 1)
        val older = AppVersionIos("9.99.0", 2)
        assertTrue(newer.isNewerThan(older) == true)
    }

    @Test
    fun `isNewerThan iOS major2`() {
        val newer = AppVersionIos("1.0.1", 1)
        val older = AppVersionIos("0.99.0", 2)
        assertTrue(newer.isNewerThan(older) == true)
    }

    @Test
    fun `isNewerThan iOS different lengths`() {
        val newer = AppVersionIos("1.0", 1)
        val older = AppVersionIos("1", 2)
        assertTrue(newer.isNewerThan(older) == true)
    }

    @Test
    fun `isNewerThan iOS invalid data returns null`() {
        val newer = AppVersionIos("1.a", 1)
        val older = AppVersionIos("1", 2)
        assertNull(newer.isNewerThan(older))
    }

    @Test
    fun `isNewerThan different platforms returns null`() {
        val newer = AppVersionAndroid("unused1", 2)
        val older = AppVersionIos("1.2.0", 1)
        assertNull(newer.isNewerThan(older))
    }
}