package wallapp.string

import android.graphics.Typeface
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.text.HtmlCompat
import wallapp.phrase.Phrase
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

actual fun String.fmt(vararg args: Any?): String = format(*args)

fun Spanned.getStyleSpanCount(spanStyle: Int): Int {
    val styleSpans: Array<StyleSpan> = getSpans(0, length, StyleSpan::class.java)
    return styleSpans.filter { it.style and spanStyle == spanStyle }.size
}

fun CharSequence.asBoldSpan() : SpannableString {
    val spannableString = SpannableString(this)
    if (isBlank()) return spannableString
    spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}

fun Phrase.putHtmlKeys(): Phrase = putOptional("bold_start", "<b>")
    .putOptional("bold_end", "</b>")
    .putOptional("italics_start", "<i>")
    .putOptional("italics_end", "</i>")

fun Phrase.toHtmlSpanned() = putHtmlKeys()
    .format()
    .toHtmlSpanned()
fun CharSequence.toHtmlSpanned() = toString().toHtmlSpanned()
fun String.toHtmlSpanned() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)

fun String.stripHtml(): String =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this).toString()
    }

fun String.asHtmlSpannableString(): SpannableString =
    SpannableString(if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    })

fun String?.isValidUrl() : Boolean {
    if (this == null) return false
    return URLUtil.isNetworkUrl(this) && Patterns.WEB_URL.matcher(this).matches()
}

fun String.asDurationString(): String {
    return try {
        (toLong() * 1000).asDurationString()
    } catch (e: NumberFormatException) {
        this
    }
}

fun Long.asDurationString(): String {
    val seconds = this.seconds.inWholeSeconds
    val absSeconds = abs(seconds)
    val hours = absSeconds / 3600
    val minutes = absSeconds % 3600 / 60
    val positive = if (hours == 0L) {
        String.format("%02d:%02d", minutes, absSeconds % 60)
    } else {
        String.format("%d:%02d:%02d", hours, minutes, absSeconds % 60)
    }
    return if (seconds < 0) "-$positive" else positive
}
