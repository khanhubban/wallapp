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
expect fun AutoResizeTextCompat(
    text: String,
    fontSizeRange: FontSizeRange,
    style: TextStyle/* = LocalTextStyle.current*/,      // KMM is complaining about this, so require it be passed
    modifier: Modifier /*= Modifier */,
    color: Color /*= Color.Unspecified*/,
    typeface: Typeface? /*= null*/,
    fontStyle: FontStyle? /*= null*/,
    fontWeight: FontWeight? /*= null*/,
//    fontFamily: FontFamily? = null,
//    letterSpacing: TextUnit = TextUnit.Unspecified,
//    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? /*= null*/,
    centerVertically: Boolean /*= true*/,
//    lineHeight: TextUnit = TextUnit.Unspecified,
//    overflow: TextOverflow = TextOverflow.Clip,
//    softWrap: Boolean = true,
    maxLines: Int /*= Int.MAX_VALUE*/,
)