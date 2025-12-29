package wallapp.pixel.font

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import wallapp.pixel.typeface.Typeface
import wallapp.pixel.typeface.TypefaceRepository

fun TypefaceRepository.map(
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
): Typeface? {
    if (fontStyle == null) return regularTypeface

    val isNormal = fontStyle == FontStyle.Normal
    val isItalic = fontStyle == FontStyle.Italic
    val isBold = fontWeight == FontWeight.Bold
    require(isNormal || isBold)
    return if (isBold) {
        boldTypeface
    } else if (isItalic) {
        TODO("Add support for italic typeface")
    } else {
        regularTypeface
    }
}