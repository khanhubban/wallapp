package wallapp.ui.content.showcase.typeface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.font.TextStyle
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.mapTextStyle
import wallapp.text.TextAlign

data class TextPreview(
    val textStyle: TextStyle,
    val text: Text,
)

private fun create(textStyle: TextStyle, label: String): TextPreview {
    return TextPreview(
        textStyle = textStyle,
        text = TextStyleBody(string = "$label:", textAlign = TextAlign.Start),
    )
}

@Composable
fun TextStylesPreview(
    message: String = "A quick brown Fox",
) {
    val items = listOf(
        create(TextStyle.Caption, "Caption"),
        create(TextStyle.Body, "Body"),
        create(TextStyle.Subheading, "Subhead"),
        create(TextStyle.SubheadingActive, "Subhead Active"),
        create(TextStyle.CallToAction, "CallToAction"),
        create(TextStyle.Headline, "Headline"),
        create(TextStyle.Display, "Display"),
    )

    Column {
        items.forEach {
            TextPreview(message, it)
        }
    }
}

@Composable
private fun TextPreview(message: String, preview: TextPreview) {
    val messageText = mapTextStyle(
        string = message,
        textStyle = preview.textStyle,
        textAlign = TextAlign.Start,
    )
    Column {
        Text(preview.text)
        Spacer(modifier = Modifier.height(2.dp))
        Text(messageText)
        Spacer(modifier = Modifier.height(8.dp))
    }
}