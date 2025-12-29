package wallapp.ui.content.placeholder

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun PlaceholderLabel(
    text: String = "(placeholder)",
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(
        text,
        style = style,
        color = color,
        modifier = modifier,
    )
}