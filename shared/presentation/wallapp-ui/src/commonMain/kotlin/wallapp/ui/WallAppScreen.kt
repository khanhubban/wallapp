package wallapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import wallapp.app.AppUiState
import wallapp.app.AppViewState
import wallapp.pixel.alert.AlertDialog
import wallapp.pixel.compose.BackHandler
import wallapp.pixel.compose.collectAsState
import wallapp.pixel.navigation.NavigationEvent
import wallapp.pixel.render.Render
import wallapp.pixel.render.RenderLocalProvider
import wallapp.theme.Theme
import wallapp.ui.navigation.NavigationEventHandler

@Composable
fun WallAppScreen(
    render: Render,
    screenControllers: ScreenControllers,
    appUiState: AppUiState,
    typography: Typography,
    typography3rdParty: Typography,
    modifier: Modifier = Modifier,
) {
    val appViewState by appUiState.appViewState.collectAsState()
    val navigationEvent = appUiState.navigationEvent
    val theme by appUiState.theme.collectAsState()

    WallAppScreen(
        render,
        screenControllers,
        appViewState = appViewState,
        navigationEvent = navigationEvent,
        theme = theme,
        modifier = modifier,
        typography = typography,
        typography3rdParty = typography3rdParty,
    )
}

@Composable
fun WallAppScreen(
    render: Render,
    screenControllers: ScreenControllers,
    appViewState: AppViewState,
    navigationEvent: Flow<NavigationEvent>,
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
                WallAppScreenRoot(
                    render,
                    screenControllers,
                    appViewState,
                    navigationEvent,
                )
            }
        }
    }
}


@Composable
fun WallAppScreenRoot(
    render: Render,
    screenControllers: ScreenControllers,
    appViewState: AppViewState,
    navigationEvent: Flow<NavigationEvent>,
) {
    val screenNavigator = screenControllers.screenNavigator
    val backHandlerEnabled = appViewState.backHandlerEnabled
    val dialog = appViewState.alertViewState
    val onBack = appViewState.onBack

    BackHandler(
        enabled = backHandlerEnabled,
        onBack = onBack,
    )

    NavigationEventHandler(
        screenNavigator,
        navigationEvent,
    )

    AppScreen(
        render,
        appViewState.screenViewState,
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    )

    if (dialog != null) {
        AlertDialog(
            render,
            dialog,
        )
    }
}
