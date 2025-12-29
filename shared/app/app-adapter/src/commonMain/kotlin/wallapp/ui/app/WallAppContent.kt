package wallapp.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.app.AppUiState

@Composable
expect fun WallAppContent(
    modifier: Modifier,
    appUiState: AppUiState,
)
