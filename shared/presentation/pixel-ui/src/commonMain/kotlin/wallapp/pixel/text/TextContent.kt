package wallapp.pixel.text

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeDefaults
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import wallapp.pixel.compose.conditional
import wallapp.pixel.font.FontWeightMapper
import wallapp.pixel.font.TextStyleMapper
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.text.composeTextAlign
import androidx.compose.material3.Text as TextMaterial3

/**
 * [colorOverride]: If not null, this color will be used instead of the color defined in the [text].
 */
@Composable
fun Text(
    text: Text,
    modifier: Modifier = Modifier,
    colorOverride: Color? = null,
) {
    val showDebug = Text.showDebug
    if (showDebug) {
        text.TextDebug(modifier, colorOverride)
    } else {
        text.Internal(
            modifier = modifier,
            colorOverride = colorOverride,
        )
    }
}

@Composable
fun TextCentered(
    text: Text,
    height: Dp,
    modifier: Modifier = Modifier,
    colorOverride: Color? = null,
) {
    Box(
        modifier = modifier
            .height(height),
    ) {
        Text(
            text,
            modifier = Modifier
                .align(Alignment.Center),
            colorOverride = colorOverride,
        )
    }
}

@Composable
internal fun Text.Internal(
    modifier: Modifier = Modifier,
    colorOverride: Color? = null,
) {
    when (this) {
        is Text.Fixed -> Text(modifier, colorOverride)
        is Text.AutoSize -> Text(modifier, colorOverride)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Text.Fixed.Text(
    modifier: Modifier = Modifier,
    colorOverride: Color? = null,
) {
    val string = string
    val style = TextStyleMapper.map(style)
    val color = colorOverride ?: colorToken?.let { ThemeColorTypeMapper.map(it) } ?: Color.Unspecified
    val fontWeight = fontWeight?.let { FontWeightMapper.map(it) }
    val ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling
    val maxLines = maxLines ?: Int.MAX_VALUE
    val textAlign = textAlign?.composeTextAlign
    val useMarquee = useMarquee
    val marqueeSpacing = marqueeSpacing
    val animateMarquee = animateMarquee

    val density: Density = LocalDensity.current
    val fontSize = if (ignoreLargeSystemFontScaling && density.fontScale > 1f) {
        // If the user has a large system font scaling, we want to ignore it for this text.
        // Handle this by dividing the font size by the font scale.
        (style.fontSize / density.fontScale)
//            .also {
//                Log.d("[Text] fontSize: $it, density: $density")
//            }
    } else {
        TextUnit.Unspecified
    }

//    Log.d("[Text] string: $string, textAlign: $textAlign, text: $this")
    TextMaterial3(
        text = string,
        modifier = modifier.conditional(useMarquee) {
            basicMarquee(
                spacing = MarqueeSpacing(marqueeSpacing!!),
                iterations = if (animateMarquee) MarqueeDefaults.Iterations else 0
            )
        },
        color = color,
        style = style,
        fontWeight = fontWeight,
        fontSize = fontSize,
        textAlign = textAlign,
        maxLines = maxLines,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Text.AutoSize.Text(
    modifier: Modifier = Modifier,
    colorOverride: Color? = null,
) {
    val string = string
    val style = TextStyleMapper.map(style)
    val color = colorOverride ?: colorToken?.let { ThemeColorTypeMapper.map(it) } ?: Color.Unspecified
    val fontWeight = fontWeight?.let { FontWeightMapper.map(it) }
    val maxLines = maxLines ?: Int.MAX_VALUE
    val textAlign = textAlign?.composeTextAlign
    val fontSizeRange = fontSizeRange

//    Log.d("[Text] string: $string, textAlign: $textAlign, text: $this")
    AutoResizeText(
        text = string,
        modifier = modifier,
        fontSizeRange = fontSizeRange,
        color = color,
        style = style,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
    )
}
