package wallapp.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.pixel.text.Text

@Composable
fun TextDivider(
    text: Text,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Divider(modifier = Modifier.weight(1f))

        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Divider(modifier = Modifier.weight(1f))
    }
}