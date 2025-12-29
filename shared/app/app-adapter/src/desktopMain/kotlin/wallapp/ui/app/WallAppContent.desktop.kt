package wallapp.ui.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import wallapp.app.AppLaunchManager
import wallapp.app.AppUiState
import wallapp.di.resolveDependency
import wallapp.pixel.render.Render
import wallapp.pixel.system.window.updateInsets
import wallapp.system.window.WindowFrameManager
import wallapp.ui.Render
import wallapp.ui.WallAppNavigation
import wallapp.ui.WallAppScreens

@Composable
actual fun WallAppContent(
    modifier: Modifier,
    appUiState: AppUiState,
) {
    val render: Render = remember { Render() }
    val typography = MaterialTheme.typography //  AppTypography()
    val typography3rdParty: Typography = MaterialTheme.typography // Typography3rdParty()

    resolveDependency<WindowFrameManager>().updateInsets()
    resolveDependency<AppLaunchManager>().run()

    WallAppNavigation(appUiState) { screenControllers ->
        WallAppScreens(
            render,
            screenControllers,
            appUiState,
            typography = typography,
            typography3rdParty = typography3rdParty,
            modifier,
        )
    }
}