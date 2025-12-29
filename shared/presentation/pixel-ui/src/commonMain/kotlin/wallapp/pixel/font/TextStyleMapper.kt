package wallapp.pixel.font

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import wallapp.font.TextStyle
import androidx.compose.ui.text.TextStyle as MaterialTextStyle

object TextStyleMapper {

    @Composable
    fun map(textStyle: TextStyle): MaterialTextStyle {
        val typography = MaterialTheme.typography
        return when (textStyle) {
            TextStyle.Caption -> typography.bodySmall
            TextStyle.Body -> typography.bodyMedium
            TextStyle.Subheading -> typography.titleSmall
            TextStyle.SubheadingActive -> typography.titleMedium
            TextStyle.CallToAction -> typography.headlineSmall
            TextStyle.Headline -> typography.titleLarge
            TextStyle.Display -> typography.headlineLarge
            // Note: other Material typographies are not used/supported.
        }
    }
}