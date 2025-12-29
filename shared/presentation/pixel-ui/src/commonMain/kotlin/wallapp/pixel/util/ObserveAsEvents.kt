package wallapp.pixel.util

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Composable
expect fun <T> ObserveAsEvents(
    flow: Flow<T>,
    onEvent: (T) -> Unit,
)