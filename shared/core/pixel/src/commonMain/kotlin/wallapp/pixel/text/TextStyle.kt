@file:Suppress("FunctionName")

package wallapp.pixel.text

import wallapp.font.TextStyle
import wallapp.text.TextAlign
import wallapp.theme.ColorToken

private val textMapper: TextMapper = TextMapper

fun mapTextStyle(
    textStyle: TextStyle,
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = false,
    useMarquee: Boolean = false,
    animateMarquee: Boolean = true,
): Text = textMapper.map(
    string = string,
    textStyle,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
    useMarquee = useMarquee,
    animateMarquee = animateMarquee,
)

fun TextStyleCaption(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = false,
): Text = mapTextStyle(
    textStyle = TextStyle.Caption,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
)

fun TextStyleBody(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = false,
): Text = mapTextStyle(
    textStyle = TextStyle.Body,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
)

fun TextStyleBodyResizeable(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    colorToken: ColorToken? = null,
): Text = textMapper.mapStyleBodyResizeable(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
    colorToken = colorToken,
)

fun TextStyleSubheading(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = false,
    useMarquee: Boolean = false,
): Text = mapTextStyle(
    textStyle = TextStyle.Subheading,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
    useMarquee = useMarquee,
)

fun TextStyleSubheadingActive(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = false,
    useMarquee: Boolean = false,
): Text = mapTextStyle(
    textStyle = TextStyle.SubheadingActive,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
    useMarquee = useMarquee,
)

fun TextStyleCallToAction(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = false,
    useMarquee: Boolean = false,
    animateMarquee: Boolean = true,
): Text = mapTextStyle(
    textStyle = TextStyle.CallToAction,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
    useMarquee = useMarquee,
    animateMarquee = animateMarquee,
)

fun TextStyleHeadline(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    useMarquee: Boolean = false,
    ignoreLargeSystemFontScaling: Boolean = false,
): Text = mapTextStyle(
    textStyle = TextStyle.Headline,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
    useMarquee = useMarquee,
)

fun TextStyleDisplay(
    string: String,
    textAlign: TextAlign? = null,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
    ignoreLargeSystemFontScaling: Boolean = true,
): Text = mapTextStyle(
    textStyle = TextStyle.Display,
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
)
