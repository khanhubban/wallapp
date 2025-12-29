package wallapp.displaycutout

import wallapp.math.geometry.Rect
import wallapp.type.Side
import wallapp.utils.WindowDimensMock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class DisplayCutoutsTest {

    @Test fun `to and from exportString`() {
        val displayCutouts = DisplayCutouts(
            topCutout = DisplayCutout(Rect(0f, 0f, 160f, 160f)),
            leftCutout = null,
            rightCutout = null,
        )

        val exportString = displayCutouts.exportString
        assertEquals(displayCutouts, DisplayCutouts.fromExportString(exportString))
    }

    @Test fun `empty exportString`() {
        assertNull(DisplayCutouts.fromExportString(""))
    }

    @Test fun `getPreferredCutoutSide() top`() {
        val windowDimens = WindowDimensMock(_maxDisplayDimension = 1920, _minDisplayDimension = 1080)

        assertEquals(
            Side.Left,
            DisplayCutouts(
                topCutout = DisplayCutout(Rect(0f, 0f, 160f, 160f)),
                leftCutout = null,
                rightCutout = null,
            ).getPreferredCutoutSide(windowDimens)
        )

        assertEquals(
            Side.Right,
            DisplayCutouts(
                topCutout = DisplayCutout(Rect(
                    windowDimens.minDisplayDimension - 160f,
                    0f,
                    windowDimens.minDisplayDimension.toFloat(),
                    160f)),
                leftCutout = null,
                rightCutout = null,
            ).getPreferredCutoutSide(windowDimens)
        )

        assertNull(DisplayCutouts(
                topCutout = DisplayCutout(Rect(
                    windowDimens.minDisplayDimension / 2 - 80f,
                    0f,
                    windowDimens.minDisplayDimension / 2 + 80f,
                    160f)),
                leftCutout = null,
                rightCutout = null,
            ).getPreferredCutoutSide(windowDimens)
        )
    }

    @Test fun `getPreferredCutoutSide() left returns Side-Left`() {
        val displayCutouts = DisplayCutouts(
            topCutout = null,
            leftCutout = DisplayCutout(Rect(0f, 20f, 160f, 160f)),
            rightCutout = null,
        )
        val windowDimens = WindowDimensMock(_maxDisplayDimension = 1920, _minDisplayDimension = 1080)
        assertEquals(Side.Left, displayCutouts.getPreferredCutoutSide(windowDimens))
    }

    @Test fun `getPreferredCutoutSide() right returns Side-Right`() {
        val windowDimens = WindowDimensMock(_maxDisplayDimension = 1920, _minDisplayDimension = 1080)
        val displayCutouts = DisplayCutouts(
            topCutout = null,
            leftCutout = null,
            rightCutout = DisplayCutout(Rect(
                windowDimens.minDisplayDimension - 160f,
                0f,
                windowDimens.minDisplayDimension.toFloat(),
                160f)),
        )
        assertEquals(Side.Right, displayCutouts.getPreferredCutoutSide(windowDimens))
    }
}