package wallapp.pixel.text

import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import wallapp.pixel.typeface.Typeface
import wallapp.pixel.typeface.typefaceSystem
import android.graphics.Typeface as TypefaceSystem

@Composable
actual fun AutoResizeTextCompat(
    text: String,
    fontSizeRange: FontSizeRange,
    style: TextStyle,
    modifier: Modifier,
    color: Color,
    typeface: Typeface? /*= null*/,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
//    fontFamily: FontFamily? = null,
//    letterSpacing: TextUnit = TextUnit.Unspecified,
//    textDecoration: TextDecoration? = null,
    textAlign: TextAlign?,
    centerVertically: Boolean,
//    lineHeight: TextUnit = TextUnit.Unspecified,
//    overflow: TextOverflow = TextOverflow.Clip,
//    softWrap: Boolean = true,
    maxLines: Int,
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }
    val typefaceSystem: TypefaceSystem? = typeface?.typefaceSystem

    var contentHeight by remember { mutableIntStateOf(-1) }
    var contentWidth by remember { mutableIntStateOf(-1) }

    AndroidView(
        modifier = modifier
            .onGloballyPositioned {
                contentWidth = it.size.width
                contentHeight = it.size.height
            },
        factory = { context ->
            TextView(context).apply {
                if (typefaceSystem != null) {
                    this.typeface = typefaceSystem
                }
            }
        },
        update = { textView ->
            if (contentWidth > -1) {
                textView.width = contentWidth
            }
            if (contentHeight > -1) {
                textView.height = contentHeight
            }

            textView.text = text

            textView.applyFontWeightAndStyle(fontWeight, fontStyle)

            if (textAlign != null) {
                textView.applyTextAlign(textAlign, centerVertically = centerVertically)
            }

            textView.setTextColor(textColor.toArgb())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textView.setAutoSizeTextTypeUniformWithConfiguration(
                    fontSizeRange.min.value.toInt(),
                    fontSizeRange.max.value.toInt(),
                    fontSizeRange.step.value.toInt(),
                    TypedValue.COMPLEX_UNIT_SP,
                )
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeRange.max.value)
            }

            textView.maxLines = maxLines
        }
    )
}