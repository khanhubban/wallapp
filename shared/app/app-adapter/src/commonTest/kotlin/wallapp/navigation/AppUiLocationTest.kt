package wallapp.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class AppUiLocationTest {

    @Test fun verifyAppUiLocationCodesAreUnique() {
        val entries = AppUiLocation.entries.toTypedArray()
        val codes = entries.map { it.code }.distinct()
        assertEquals(entries.size, codes.size, "Duplicate AppReferrer.code")
    }

}