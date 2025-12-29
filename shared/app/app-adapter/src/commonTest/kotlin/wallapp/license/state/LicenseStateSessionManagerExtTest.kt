package wallapp.license.state

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LicenseStateSessionManagerExtTest {

    @Test fun extractIdFromAppUrl() {
        assertEquals("12-3", extractIdFromAppUrl("https://example.com/w/12-3"))
        assertEquals("12-3", extractIdFromAppUrl("https://example.com/w/12-3?a=b"))
        assertNull(extractIdFromAppUrl("https://example.io/w/123?a=b"))
        assertNull(extractIdFromAppUrl("http://example.io/w/123?a=b"))
        assertNull(extractIdFromAppUrl("http://example.io/123?a=b"))
        assertNull(extractIdFromAppUrl("http://example.io/W?123"))
    }

    @Test fun isUuid() {
        assertTrue(isUuid("cf5c1fca-5fd6-4aae-b889-9154f43f2df6"))
        assertFalse(isUuid("cf5c1fca-5fd6-4aae-b889-154f43f2df6"))
        assertFalse(isUuid("foo"))
    }
}