package wallapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render
import wallapp.ui.ScreenControllers

@Composable
fun NavigationScreens(
    render: Render,
    screenControllers: ScreenControllers,
) {
    NavigationScreens(
        render = render,
        screenControllers = screenControllers,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
expect fun NavigationScreens(
    render: Render,
    screenControllers: ScreenControllers,
    modifier: Modifier,
)
