package wallapp.pixel.text

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.NSTextAlignmentJustified
import platform.UIKit.NSTextAlignmentLeft
import platform.UIKit.NSTextAlignmentNatural
import platform.UIKit.NSTextAlignmentRight
import platform.UIKit.UILabel
import wallapp.pixel.compose.uiColor
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

@OptIn(ExperimentalForeignApi::class)
@Composable
fun AutoResizeTextUIKit(
    text: String,
    fontSizeRange: FontSizeRange,
    style: TextStyle,
    modifier: Modifier,
    color: Color,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    textAlign: TextAlign?,
    centerVertically: Boolean,
    maxLines: Int,
) {
    val backgroundColor = MaterialTheme.colorScheme.surface.uiColor
    val onBackground = color.uiColor

    val minScaleFactor = fontSizeRange.min.value / fontSizeRange.max.value

    fun UILabel.update() {
        this.text = text
//        this.backgroundColor = UIColor.clearColor
        this.backgroundColor = backgroundColor
        this.textColor = onBackground

//        Log.d("Textcolor: $color, text: $text")

        this.numberOfLines = maxLines.toLong()
        this.adjustsFontSizeToFitWidth = true
        this.minimumScaleFactor = minScaleFactor.toDouble()

        this.textAlignment = when (textAlign) {
            TextAlign.Left -> NSTextAlignmentLeft
            TextAlign.Right -> NSTextAlignmentRight
            TextAlign.Center -> NSTextAlignmentCenter
            TextAlign.Justify -> NSTextAlignmentJustified
            else -> NSTextAlignmentNatural
        }
    }

    UIKitView(
        modifier = modifier
            .background(Color.Transparent),
        factory = {
            UILabel().apply { update() }
        },
        update = { uiLabel ->
            uiLabel.apply { update() }
        }
    )
}