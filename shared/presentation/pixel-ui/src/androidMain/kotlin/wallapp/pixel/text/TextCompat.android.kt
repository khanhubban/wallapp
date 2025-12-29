package wallapp.pixel.text

import android.view.Gravity
import android.widget.TextView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

fun TextView.applyFontWeightAndStyle(fontWeight: FontWeight?, fontStyle: FontStyle?) {
    if (fontWeight == null && fontStyle == null) return

    if (fontWeight == FontWeight.Bold && fontStyle == FontStyle.Italic) {
        setTypeface(typeface, android.graphics.Typeface.BOLD_ITALIC)
    } else if (fontWeight == FontWeight.Bold) {
        setTypeface(typeface, android.graphics.Typeface.BOLD)
    } else if (fontStyle == FontStyle.Italic) {
        setTypeface(typeface, android.graphics.Typeface.ITALIC)
    } else if (fontWeight == FontWeight.Normal) {
        setTypeface(typeface, android.graphics.Typeface.NORMAL)
    }
}

fun TextView.applyTextAlign(textAlign: TextAlign, centerVertically: Boolean = true) {
    this.gravity = when (textAlign) {
        TextAlign.Start -> Gravity.START
        TextAlign.End -> Gravity.END
        TextAlign.Center -> Gravity.CENTER
        TextAlign.Justify -> Gravity.CENTER_HORIZONTAL
        TextAlign.Left -> Gravity.LEFT
        TextAlign.Right -> Gravity.RIGHT
        else -> 0
    } or (if (centerVertically) Gravity.CENTER_VERTICAL else 0)
}
