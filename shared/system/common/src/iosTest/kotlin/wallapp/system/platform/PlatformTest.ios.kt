package wallapp.system.platform

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTestIos {

    @Test
    fun `platform name iOS`() {
        assertTrue(PlatformIos().name.contains("iOS"))
    }
}