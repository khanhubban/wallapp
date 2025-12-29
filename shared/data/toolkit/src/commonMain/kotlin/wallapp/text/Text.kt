@file:Suppress("FunctionName")

package wallapp.text

import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleBodyResizeable
import wallapp.pixel.text.TextStyleCallToAction
import wallapp.pixel.text.TextStyleCaption
import wallapp.pixel.text.TextStyleDisplay
import wallapp.pixel.text.TextStyleHeadline
import wallapp.pixel.text.TextStyleSubheading
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.theme.ColorToken

fun TextButtonLabel(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
    colorToken: ColorToken? = ColorToken.ThemeOnPrimary,
    ignoreLargeSystemFontScaling: Boolean = true,
    maxLines: Int? = null,
    useMarquee: Boolean = false,
    animateMarquee: Boolean = true,
) : Text = TextStyleCallToAction(
        string = string,
        textAlign = textAlign,
        colorToken = colorToken,
        ignoreLargeSystemFontScaling = ignoreLargeSystemFontScaling,
        maxLines = maxLines,
        useMarquee = useMarquee,
        animateMarquee = animateMarquee,
    )

fun TextPlaceholder(string: String, textAlign: TextAlign? = TextAlign.Center): Text =
    TextStyleBody(
        string = string,
        textAlign = textAlign,
    )

fun TextCaption(string: String, textAlign: TextAlign? = null): Text =
    TextStyleCaption(string, textAlign)

fun TextTabSelected(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
): Text = TextStyleSubheadingActive(string, textAlign)

fun TextTabUnselected(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
): Text = TextStyleSubheading(string, textAlign)


fun TextErrorTitle(string: String, textAlign: TextAlign? = TextAlign.Center): Text =
    TextDisplay(
        string = string,
        textAlign = textAlign,
    )

fun TextErrorBody(string: String, textAlign: TextAlign? = TextAlign.Center): Text =
    TextStyleBody(
        string = string,
        textAlign = textAlign,
    )

fun TextBodySmallResizable(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
): Text = TextStyleBodyResizeable(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextHint(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
): Text = TextStyleCaption(
    string = string,
    textAlign = textAlign,
)

fun TextEmail(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
): Text = TextStyleBody(
    string = string,
    textAlign = textAlign,
)

fun TextExhibit(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
): Text = TextStyleDisplay(
    string = string,
    textAlign = textAlign,
)

fun TextSignInButton(string: String): Text = TextStyleSubheadingActive(
    string = string,
    textAlign = TextAlign.Center,
    maxLines = 1,
)

fun TextSelection(
    string: String,
    textAlign: TextAlign? = TextAlign.Start,
    colorToken: ColorToken? = null,
    maxLines: Int? = null,
): Text = TextStyleBodyResizeable(
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
)

fun TextFeedTitle(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
    maxLines: Int? = null,
    autoResize: Boolean = false,
) : Text {
    return TextStyleHeadline(
        string = string,
        textAlign = textAlign,
        maxLines = maxLines,
    )
}

fun TextToolbarTitle(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
    colorToken: ColorToken? = null,
    maxLines: Int? = 1,
    useMarquee: Boolean = true,
): Text = TextStyleHeadline(
    string = string,
    textAlign = textAlign,
    colorToken = colorToken,
    maxLines = maxLines,
    useMarquee = useMarquee,
)

fun TextCollectionFooter(
    string: String,
    textAlign: TextAlign? = null,
): Text = TextStyleBody(
    string = string,
    textAlign = textAlign,
    maxLines = 1,
)

fun TextNavBarTab(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
): Text = TextStyleCaption(
    string = string,
    textAlign = textAlign,
    ignoreLargeSystemFontScaling = true,
)

fun TextSettingSubtitle(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = 1,
): Text = TextStyleBody(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextSettingTitle(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = 1,
): Text = TextStyleSubheading(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextSettingSubheading(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = 1,
): Text = TextStyleBody(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextSettingButton(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = 1,
): Text = TextStyleCallToAction(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextSettingHeading(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = 1,
): Text = TextStyleHeadline(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextDisplay(
    string: String,
    textAlign: TextAlign? = TextAlign.Center,
    maxLines: Int? = null,
    autoResize: Boolean = false,
): Text = TextStyleDisplay(
    string = string,
    textAlign = textAlign,
    maxLines = maxLines,
)

fun TextTitle(
    string: String,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    useMarquee: Boolean = false,
): Text = TextStyleHeadline(
    string = string,
    textAlign = textAlign,
    colorToken = null,
    maxLines = maxLines,
    useMarquee = useMarquee,
)