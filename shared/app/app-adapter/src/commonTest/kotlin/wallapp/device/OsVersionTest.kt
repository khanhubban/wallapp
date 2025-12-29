package wallapp.device

import kotlin.test.Test
import kotlin.test.assertEquals


class OsVersionTest {

    @Test
    fun `compareOsVersion numeric`() {
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("1.0.0", "0.99.0"))
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("16.1.0", "16.0.10"))
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("17.4", "17.3.12"))
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("13.2", "13"))
    }

    @Test
    fun `compareOsVersion equal versions`() {
        assertEquals(OsVersionCompareResult.Equal, compareOsVersion("2.0.0", "2.0.0"))
        assertEquals(OsVersionCompareResult.Equal, compareOsVersion("3.5.1", "3.5.1"))
    }

    @Test
    fun `compareOsVersion longer version strings`() {
        assertEquals(OsVersionCompareResult.Older, compareOsVersion("1.2.3.4", "1.2.3.4.5"))
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("1.2.3.4.6", "1.2.3.4"))
    }

    @Test
    fun `compareOsVersion non-numeric segments`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.2a", "1.2b"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.2b", "1.2a"))
    }

    @Test
    fun `compareOsVersion empty strings`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0.0", ""))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("", "1.0.0"))
        assertEquals(OsVersionCompareResult.Equal, compareOsVersion("", ""))
    }

    @Test
    fun `compareOsVersion bracket characters`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0.0 (2)", "1.0.0 (1)"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0.1 (1)", "1.0.0 (10)"))
    }

    @Test
    fun `compareOsVersion versions with leading zeros`() {
        assertEquals(OsVersionCompareResult.Equal, compareOsVersion("01.01.01", "1.1.1"))
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("02.01.01", "2.0.9"))
    }

    @Test
    fun `compareOsVersion versions with non-digit suffixes`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0beta", "1.0alpha"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0.1alpha", "1.0.1"))
    }

    @Test
    fun `compareOsVersion versions with spacing or special characters`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0. 1", "1.0.1"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0!1", "1.0.1"))
    }

    @Test
    fun `compareOsVersion versions with large numbers`() {
        assertEquals(OsVersionCompareResult.Newer, compareOsVersion("1.0.1000000000", "1.0.999999999"))
        assertEquals(OsVersionCompareResult.Older, compareOsVersion("2.0.999999999", "2.1.0"))
    }

    @Test
    fun `compareOsVersion versions with multiple decimal points`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.0..1", "1.0.1"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1..0.1", "1.0.1"))
    }

    @Test
    fun `compareOsVersion mixed numeric and alphabetic characters`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.2a3", "1.2a2"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.2a", "1.2"))
    }

    @Test
    fun `compareOsVersion words`() {
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("one", "two"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("1.2", "one"))
        assertEquals(OsVersionCompareResult.Unknown, compareOsVersion("one", "1.2"))
    }

    @Test
    fun `coarsenMajorMinor keeps major and minor`() {
        assertEquals("17.5", "17.5.1".coarsenMajorMinor())
        assertEquals("16.0", "16.0.9".coarsenMajorMinor())
    }

    @Test
    fun `coarsenMajorMinor handles major only`() {
        assertEquals("14", "14".coarsenMajorMinor())
    }

    @Test
    fun `coarsenMajorMinor keeps invalid strings`() {
        assertEquals("17.0beta", "17.0beta".coarsenMajorMinor())
        assertEquals("", "".coarsenMajorMinor())
    }

}
