package wallapp.ads

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class AdErrorTest {

    @Test fun `to and from export string`() {
        val original = AdError(AdErrorCode.MediationNoFill, "a message", "a domain")
        val exportString = original.exportString
        val fromExportString = AdError.fromExportString(exportString)
        assertEquals(original, fromExportString)
    }

    @Test fun `from empty string`() {
        assertNull(AdError.fromExportString(""))
    }

}