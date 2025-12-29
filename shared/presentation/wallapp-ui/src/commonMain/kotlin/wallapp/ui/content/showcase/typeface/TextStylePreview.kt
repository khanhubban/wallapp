package wallapp.ui.content.showcase.typeface

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import wallapp.pixel.text.TextM3

private data class TextStylePreview(
    val style: TextStyle,
    val type: String,
)

@Composable
fun TextStylePreview(
    message: String = "A quick brown Fox",
    typography: Typography = MaterialTheme.typography,
) {
    val items = listOf(
//        TextStylePreview(typography.labelSmall, "labelSmall"),
//        TextStylePreview(typography.labelMedium, "labelMedium"),
//        TextStylePreview(typography.labelLarge, "labelLarge"),

        TextStylePreview(typography.bodySmall, "bodySmall"),
        TextStylePreview(typography.bodyMedium, "bodyMedium"),
        TextStylePreview(typography.bodyLarge, "bodyLarge"),

//        TextStylePreview(typography.titleSmall, "titleSmall"),
//        TextStylePreview(typography.titleMedium, "titleMedium"),
        TextStylePreview(typography.titleLarge, "titleLarge"),

        TextStylePreview(typography.headlineSmall, "headlineSmall"),
//        TextStylePreview(typography.headlineMedium, "headlineMedium"),
        TextStylePreview(typography.headlineLarge, "headlineLarge"),

//        TextStylePreview(typography.displaySmall, "displaySmall"),
//        TextStylePreview(typography.displayMedium, "displayMedium"),
        TextStylePreview(typography.displayLarge, "displayLarge"),
    )
    
    Column {
        items.forEach {
            TextPreview(message, it)
        }
    }
}

@Composable
private fun TextPreview(message: String, preview: TextStylePreview) {
    Column {
        TextM3("${preview.type}:", style = MaterialTheme.typography.labelMedium)
        TextM3(message, style = preview.style)
    }
}