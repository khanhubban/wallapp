package wallapp.pixel.separator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import wallapp.pixel.compose.ifNonNull

@Composable
fun Separator(
    viewState: SeparatorViewState,
    modifier: Modifier = Modifier,
) {
    Separator(
        width = viewState.width,
        height = viewState.height,
        modifier = modifier,
    )
}

@Composable
fun Separator(
    width: Dp? = null,
    height: Dp? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .ifNonNull(width) { width(it) }
            .ifNonNull(height) { height(it) }
            .background(MaterialTheme.colorScheme.onSurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
    }
}