package wallapp.ui.widget

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

/**
 * For reasons unknown, sub-screens such as Collector and Collection display [ThemeColors.surface]
 * as the background color instead of [ThemeColors.background]. This is not an issue with the
 * Settings screen.
 *
 * This is a temporary fix until we can investigate the full problem.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfaceThemeFix(
    content: @Composable () -> Unit,
) {
    Scaffold { _ ->
        content.invoke()
    }
}