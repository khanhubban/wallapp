package wallapp.color

import android.graphics.Color
import kotlin.math.sqrt


/**
 * From https://www.alanzucconi.com/2015/09/30/colour-sorting/
 */
internal class ColorSortable(val intColor: Int, private val repetitions: Int = 12) {
    val red = Color.red(intColor)
    val green = Color.green(intColor)
    val blue = Color.blue(intColor)

    val redNormalized = red.toDouble() / 255.0
    val greenNormalized = green.toDouble() / 255.0
    val blueNormalized = blue.toDouble() / 255.0

    val hsv = FloatArray(3) {0f}.apply {
        Color.colorToHSV(intColor, this)
    }

    val luminance = sqrt( .241 * redNormalized + .691 * greenNormalized + .068 * blueNormalized)

    val sortable = mutableListOf<Int>().apply {
        val h2 = (hsv[0] * repetitions).toInt()
        var lum2 = (luminance * repetitions).toInt()
        var v2 = (hsv[2] * repetitions).toInt()
        if (h2 % 2 == 1) {
            v2 = repetitions - v2
            lum2 = repetitions - lum2
        }

        add(h2)
        add(lum2)
        add(v2)
    }
}


fun List<Int>.sortedColors(): List<Int> {
    return this.map { ColorSortable(it) }
        .sortedWith ( compareBy<ColorSortable>({ it.sortable[0] }, { it.sortable[1] }, { it.sortable[2] }))
        .map { it.intColor }
}