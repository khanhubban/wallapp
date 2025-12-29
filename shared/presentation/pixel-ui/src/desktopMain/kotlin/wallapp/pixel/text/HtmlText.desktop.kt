package wallapp.pixel.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

@Composable
actual fun HtmlText(
    html: String,
    modifier: Modifier,
    clickableLinks: Boolean,
    fontSize: TextUnit,
    color: Color,
) {
    TextM3(html, modifier)
}