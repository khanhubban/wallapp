package wallapp.pixel.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.text.Text
import androidx.compose.material3.TextButton as TextButtonMaterial

@Composable
fun TextButton(
    text: Text,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButtonMaterial(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text)
    }
}