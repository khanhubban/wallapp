package wallapp.displaycutout

import wallapp.math.geometry.PointF
import wallapp.math.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class DisplayCutoutTest {

    @Test fun `to and from exportString`() {
        val displayCutout = DisplayCutout(
            Rect(0f, 0f, 160f, 160f),
            PointF(65f, 75f),
        )

        val exportString = displayCutout.exportString
        assertEquals(displayCutout, DisplayCutout.fromExportString(exportString))
    }

    @Test fun `single argument constructor`() {
        val rect = Rect(0f, 0f, 160f, 160f)
        assertEquals(DisplayCutout(rect, PointF(80f, 80f)), DisplayCutout(rect))
    }


}