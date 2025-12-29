package wallapp.ui

import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render
import wallapp.pixel.render.RenderLocalProvider
import wallapp.pixel.view.View
import wallapp.theme.Theme

@Composable
fun WallAppSingleView(
    render: Render,
    screenControllers: ScreenControllers,
    view: View,
    theme: Theme,
    typography: Typography,
    typography3rdParty: Typography,
    modifier: Modifier,
) {
    RenderLocalProvider(render, typography3rdParty = typography3rdParty) {
        WallAppTheme(
            render = render,
            screenControllers,
            theme,
            typography,
        ) {
            Surface(
                modifier = modifier,
            ) {
                View(
                    render,
                    view,
                )
            }
        }
    }
}