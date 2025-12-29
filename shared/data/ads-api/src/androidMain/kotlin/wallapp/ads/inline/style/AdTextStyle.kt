package wallapp.ads.inline.style

import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import wallapp.annotation.ColorInt


class AdTextStyle internal constructor(
    @ColorInt val textColor: Int,
    val typeface: Typeface?,
) {

    fun applyTo(textView: TextView) {
        textView.setTextColor(textColor)
        textView.typeface = typeface
    }

    companion object {
        var DEFAULT = AdTextStyle(Color.WHITE, null)

        fun from(@ColorInt color: Int, typeface: Typeface? = null): AdTextStyle {
            return AdTextStyle(color, typeface)
        }
    }
}