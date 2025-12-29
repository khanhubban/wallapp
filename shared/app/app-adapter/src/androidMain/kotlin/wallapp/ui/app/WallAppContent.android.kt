package wallapp.ui.app

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import wallapp.app.AppUiState
import wallapp.pixel.render.Render
import wallapp.resources.AppTypography
import wallapp.resources.Typography3rdParty
import wallapp.ui.Render
import wallapp.ui.WallAppNavigation
import wallapp.ui.WallAppScreens

@Composable
actual fun WallAppContent(
    modifier: Modifier,
    appUiState: AppUiState,
) {
    val render: Render = remember { Render() }
    val typography: Typography = AppTypography()
    val typography3rdParty: Typography = Typography3rdParty()

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
