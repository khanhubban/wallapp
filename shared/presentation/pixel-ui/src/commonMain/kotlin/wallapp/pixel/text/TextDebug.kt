package wallapp.pixel.text

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import wallapp.font.TextStyle
import androidx.compose.material3.Text as TextMaterial3

val TextStyle.codeName: String
    get() = when (this) {
        TextStyle.Caption -> "C"
        TextStyle.Body -> "B"
        TextStyle.Subheading -> "SH"
        TextStyle.SubheadingActive -> "SHA"
        TextStyle.CallToAction -> "CTA"
        TextStyle.Headline -> "H"
        TextStyle.Display -> "D"
    }

@Composable
fun Text.TextDebug(
    modifier: Modifier = Modifier,
    colorOverride: Color? = null,
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Green),
    ) {
        Internal(
            modifier = modifier,
            colorOverride = colorOverride,
        )

        TextStyleDebug(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .alpha(.85f),
        )
    }
}

@Composable
fun Text.TextStyleDebug(
    modifier: Modifier = Modifier,
) {
    val string = style.codeName
    TextMaterial3(
        text = string,
        modifier = modifier
            .background(Color.Yellow)
            .padding(horizontal = 2.dp),
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
    )
}