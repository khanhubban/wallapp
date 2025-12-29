package wallapp.pixel.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import wallapp.pixel.compose.pxToDp
import wallapp.pixel.font.map
import wallapp.pixel.typeface.LocalTypefaceRepository

@Composable
fun AutoResizeText(
    text: String,
    fontSizeRange: FontSizeRange,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
//    fontFamily: FontFamily? = null,
//    letterSpacing: TextUnit = TextUnit.Unspecified,
//    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,//TextAlign.Center,
//    lineHeight: TextUnit = TextUnit.Unspecified,
//    overflow: TextOverflow = TextOverflow.Clip,
//    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    val typefaceRepository = LocalTypefaceRepository.current
    val typeface = typefaceRepository.map(fontStyle, fontWeight)

    AutoResizeTextCompat(
        text = text,
        fontSizeRange = fontSizeRange,
        style = style,
        modifier = modifier,
        color = color,
        typeface = typeface,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        textAlign = textAlign,
        centerVertically = true,
        maxLines = maxLines,
    )

//    AutoResizedText(
//        text = text,
//        modifier = modifier,
//        color = color,
//        style = style,
//        fontWeight = fontWeight ?: FontWeight.Normal,
//        textAlign = textAlign ?: TextAlign.Center,
//    )
}

/**
 * Optional container to help with sizing the [AutoResizeText]. Useful when dealing with
 * a Column or Row weight.
 *
 * Ideally this is removed when a native [AutoResizeText] solution exists.
 */
@Composable
fun AutoResizeTextContainer(
    modifier: Modifier,
    name: String,
    fontSizeRange: FontSizeRange,
    textHeightIn: Dp,
    textAlign: TextAlign,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    var labelHeight by remember { mutableStateOf(-1) }
    var labelWidth by remember { mutableStateOf(-1) }

    Box(
        modifier = modifier
            .heightIn(textHeightIn)
            .onGloballyPositioned {
                labelWidth = it.size.width
                labelHeight = it.size.height
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        if (labelWidth > 0 && labelHeight > 0) {
            AutoResizeText(
                name,
                fontSizeRange = fontSizeRange,
                maxLines = maxLines,
                style = style,
                textAlign = textAlign,
                modifier = Modifier
                    .width(labelWidth.pxToDp())
                    .height(labelHeight.pxToDp()),
            )
        }
    }
}


/**
 * Auto resize text step by step until it fits the given [fontSizeRange].
 *
 * Has a few issues, so best not to use.
 */
@Composable
internal fun AutoResizeTextStep(
    text: String,
    fontSizeRange: FontSizeRange,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    var fontSizeValue by remember { mutableStateOf(fontSizeRange.max.value) }
    var readyToDraw by remember { mutableStateOf(false) }

    TextM3(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        style = style,
        fontSize = fontSizeValue.sp,
        onTextLayout = {
            if (it.didOverflowHeight && !readyToDraw) {
                val nextFontSizeValue = fontSizeValue - fontSizeRange.step.value
                if (nextFontSizeValue <= fontSizeRange.min.value) {
                    // Reached minimum, set minimum font size and it's readyToDraw
                    fontSizeValue = fontSizeRange.min.value
                    readyToDraw = true
                } else {
                    // Text doesn't fit yet and haven't reached minimum text range, keep decreasing
                    fontSizeValue = nextFontSizeValue
                }
            } else {
                // Text fits before reaching the minimum, it's readyToDraw
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() }
    )
}

