package wallapp.displaycutout

import wallapp.math.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class DisplayCutoutTypeKtTest {

    @Test fun `inferDisplayCutoutType() punch hole Rect returns PunchHole`() {
        assertEquals(
            DisplayCutoutType.PunchHole,
            Rect(540f-40f, 0f, 540f+40f, 80f).inferDisplayCutoutType())
        assertEquals(
            DisplayCutoutType.PunchHole,
            Rect(540f-40f, 0f, 540f+40f, 120f).inferDisplayCutoutType())
        assertEquals(
            DisplayCutoutType.PunchHole,
            Rect(0f, 0f, 80f, 60f).inferDisplayCutoutType())
    }

    @Test fun `inferDisplayCutoutType() wide notch Rect returns Unknown`() {
        assertEquals(
            DisplayCutoutType.Unknown,
            Rect(540f - 140f, 0f, 540f + 140f, 80f).inferDisplayCutoutType()
        )
    }

    @Test fun `inferDisplayCutoutType() tall waterfall Rect returns Unknown`() {
        assertEquals(
            DisplayCutoutType.Unknown,
            Rect(0f, (1920/2f) - 100f, 80f, (1920/2f) + 100f).inferDisplayCutoutType()
        )
    }
}