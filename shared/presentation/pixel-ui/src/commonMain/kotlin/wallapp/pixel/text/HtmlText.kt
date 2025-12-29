package wallapp.pixel.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

@Composable
fun HtmlText(
    html: String,
) {
    HtmlText(
        html = html,
        modifier = Modifier,
        clickableLinks = true,
        fontSize = TextUnit.Unspecified,
        color = Color.Unspecified,
    )
}

@Composable
expect fun HtmlText(
    html: String,
    modifier: Modifier,
    clickableLinks: Boolean,
    fontSize: TextUnit,
    color: Color,
)