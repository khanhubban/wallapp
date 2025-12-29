package wallapp.resources

import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import java.util.Locale

fun Resources.resolveLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        with (configuration.locales) {
            if (size() > 0) {
                get(0)
            } else {
                LocaleList.getDefault().get(0)
            }
        }
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }
}

fun Resources.isXmlResource(@DrawableRes drawableId: Int): Boolean {
    val fileName = TypedValue().let {
        getValue(drawableId, it, true)
        it.string
    }
    return fileName?.toString()?.endsWith(".xml") == true
}

fun Resources.getColorCompat(@ColorRes color: Int, theme: Resources.Theme? = null): Int =
    ResourcesCompat.getColor(this, color, theme)