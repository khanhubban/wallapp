package wallapp.pixel.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import wallapp.pixel.typeface.Typeface

@Composable
actual fun AutoResizeTextCompat(
    text: String,
    fontSizeRange: FontSizeRange,
    style: TextStyle,
    modifier: Modifier,
    color: Color,
    typeface: Typeface?,
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
    TextM3(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSizeRange.max,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        textAlign = textAlign,
//        centerVertically = centerVertically,
        maxLines = maxLines,
        style = style,
    )
}