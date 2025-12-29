package wallapp.system.platform

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTestDesktop {

    @Test
    fun `platform name Desktop`() {
        assertTrue(PlatformDesktop().name.contains("Desktop"))
    }
}