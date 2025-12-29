package wallapp.pixel.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ShadowedText(
    text: String,
    modifier: Modifier = Modifier,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val textColor = Color.White
    val textShadowColor = Color.Black
    val shadowedStyle = style.copy(
        shadow = Shadow(
            color = textShadowColor,
            offset = Offset(6f, 6f),
            blurRadius = 8f,
        )
    )

    TextM3(
        text = text,
        modifier = modifier,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        color = textColor,
        textAlign = textAlign,
        style = shadowedStyle,
    )

}