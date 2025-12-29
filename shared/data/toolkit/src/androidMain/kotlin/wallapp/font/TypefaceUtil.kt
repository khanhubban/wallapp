package wallapp.font

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


fun ViewGroup.setTypeface(typeface: Typeface?) {
    if (typeface == null) return
    for (i in 0 until childCount) {
        val child: View = getChildAt(i)
        if (child is ViewGroup) {
            child.setTypeface(typeface)
            continue
        }
        if (child !is TextView) {
            continue
        }
        child.typeface = typeface
    }
}