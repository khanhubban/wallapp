package wallapp.pixel.scrim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Scrim(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = alpha))
    ) { }
}