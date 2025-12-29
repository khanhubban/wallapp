package wallapp.pixel.text

import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
actual fun HtmlText(
    html: String,
    modifier: Modifier,
    clickableLinks: Boolean,
    fontSize: TextUnit,
    color: Color,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                if (clickableLinks) {
                    movementMethod = LinkMovementMethod.getInstance()
                }
                if (fontSize.isSpecified) {
                    if (fontSize.isSp) {
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.value)
                    } else {
                        TODO("Unhandled text unit")
                    }
                }
                if (color != Color.Unspecified) {
                    val c = color.toArgb()
                    setTextColor(c)
                }
            }
        },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}