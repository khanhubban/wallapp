package wallapp.resources.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
expect fun font(
    fontName: String,
    resourceId: String,
    weight: FontWeight,
    style: FontStyle,// = FontStyle.Normal,
): Font