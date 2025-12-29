package wallapp.pixel.text

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wallapp.font.FontWeight
import wallapp.font.TextStyle
import wallapp.text.TextAlign
import wallapp.theme.ColorToken

/**
 *
 */
internal object TextMapper {

    private val DefaultMarqueeSpacing = 16.dp

    private fun mapFixed(
        string: String,
        textStyle: TextStyle,
        fontWeight: FontWeight?,
        ignoreLargeSystemFontScaling: Boolean,
        colorToken: ColorToken?,
        maxLines: Int?,
        marqueeSpacing: Dp?,
        textAlign: TextAlign?,
        animateMarquee: Boolean,
    ): Text {
        return Text.Fixed(
            string = string,
            style = textStyle,
            fontWeight = fontWeight,
            ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
            colorToken = colorToken,
            textAlign = textAlign,
            maxLines = maxLines,
            marqueeSpacing = marqueeSpacing,
            animateMarquee = animateMarquee,
        )
    }

    private fun mapResizable(
        string: String,
        textStyle: TextStyle,
        fontSizeRange: FontSizeRange,
        fontWeight: FontWeight?,
        colorToken: ColorToken?,
        maxLines: Int?,
        textAlign: TextAlign?,
    ): Text {
        return Text.AutoSize(
            string = string,
            style = textStyle,
            fontSizeRange = fontSizeRange,
            fontWeight = fontWeight,
            colorToken = colorToken,
            textAlign = textAlign,
            maxLines = maxLines,
        )
    }

    fun map(
        string: String,
        textStyle: TextStyle,
        textAlign: TextAlign?,
        fontSizeRange: FontSizeRange? = null,
        fontWeight: FontWeight? = null,
        ignoreLargeSystemFontScaling: Boolean = false,
        colorToken: ColorToken? = null,
        maxLines: Int? = null,
        useMarquee: Boolean = false,
        animateMarquee: Boolean = true,
    ): Text {
        return if (fontSizeRange != null) {
            mapResizable(
                string = string,
                textStyle = textStyle,
                fontSizeRange = fontSizeRange,
                fontWeight = fontWeight,
                colorToken = colorToken,
                maxLines = maxLines,
                textAlign = textAlign,
            )
        } else {
            mapFixed(
                string = string,
                textStyle = textStyle,
                fontWeight = fontWeight,
                ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
                colorToken = colorToken,
                maxLines = maxLines,
                marqueeSpacing = DefaultMarqueeSpacing.takeIf { useMarquee },
                textAlign = textAlign,
                animateMarquee = animateMarquee,
            )
        }
    }

    fun mapStyleBodyResizeable(
        string: String,
        textAlign: TextAlign?,
        maxLines: Int?,
        colorToken: ColorToken? = null,
    ): Text {
        return map(
            string = string,
            textStyle = TextStyle.Body,
            textAlign = textAlign,
            fontSizeRange = FontSizeRange(
                min = 7.sp,
                max = 13.sp,
            ),
            ignoreLargeSystemFontScaling = false,
            maxLines = maxLines,
            colorToken = colorToken,
        )
    }
}