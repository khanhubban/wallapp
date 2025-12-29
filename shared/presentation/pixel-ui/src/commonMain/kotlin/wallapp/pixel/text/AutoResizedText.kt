package wallapp.pixel.text

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.isUnspecified

/**
 * Composable function that automatically adjusts the size of text to fit within the given constraints.
 * From RevenueCat: https://github.com/RevenueCat/purchases-android/blob/9b13fb21a6335703622fee9ea480d6d536482bdf/ui/revenuecatui/src/main/kotlin/com/revenuecat/purchases/ui/revenuecatui/composables/AutoResizedText.kt
 */
//@SuppressWarnings("LongParameterList")
@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center,
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = style.fontSize
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = fontWeight,
            textAlign = textAlign,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .drawWithContent {
                    if (shouldDraw) {
                        drawContent()
                    }
                },
            softWrap = false,
            style = resizedTextStyle,
            onTextLayout = { result ->
                if (result.didOverflowWidth) {
                    if (style.fontSize.isUnspecified) {
                        resizedTextStyle = resizedTextStyle.copy(
                            fontSize = defaultFontSize,
                        )
                    }
                    val newFontSize = resizedTextStyle.fontSize * 0.95
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = newFontSize,
                    )
                } else {
                    shouldDraw = true
                }
            },
        )
    }
}