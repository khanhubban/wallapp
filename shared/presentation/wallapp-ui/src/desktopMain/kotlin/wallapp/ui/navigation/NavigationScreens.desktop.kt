package wallapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render
import wallapp.ui.ScreenControllers
import wallapp.ui.ScreenControllersDesktop

@Composable
actual fun NavigationScreens(
    render: Render,
    screenControllers: ScreenControllers,
    modifier: Modifier,
) {
    require(screenControllers is ScreenControllersDesktop)
    val navigator = screenControllers.navigator
    val initialScreen = screenControllers.initialScreen

    NavigationScreens(
        render = render,
        screenControllers = screenControllers,
        initialScreen = initialScreen,
        navigator = navigator,
        modifier = modifier,
    )
}
