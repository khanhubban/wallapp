package wallapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import wallapp.app.AppUiState
import wallapp.app.AppViewState
import wallapp.graphics.color
import wallapp.graphics.composeColor
import wallapp.pixel.alert.AlertDialog
import wallapp.pixel.bottomsheet.BottomSheetViewState
import wallapp.pixel.bottomsheet3.BottomSheetScaffold
import wallapp.pixel.compose.BackHandler
import wallapp.pixel.compose.collectAsState
import wallapp.pixel.globaloverlay.GlobalOverlay
import wallapp.pixel.globaloverlay.GlobalOverlayViewState
import wallapp.pixel.navigation.NavigationEvent
import wallapp.pixel.render.Render
import wallapp.pixel.render.RenderLocalProvider
import wallapp.theme.Theme
import wallapp.ui.bottomsheet.ModalBottomSheet
import wallapp.ui.navigation.NavigationEventHandler
import wallapp.ui.navigation.NavigationScreens

@Composable
fun WallAppScreens(
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

    WallAppScreens(
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
fun WallAppScreens(
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
                WallAppScreensRoot(
                    render,
                    screenControllers,
                    appViewState,
                    navigationEvent,
                )
            }
        }
    }
}

/**
 * Renders multiple app layers:
 *  [0]: the app screens
 *  [1]: a persistent bottom sheet
 *  [2]: a modal bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallAppScreensRoot(
    render: Render,
    screenControllers: ScreenControllers,
    appViewState: AppViewState,
    navigationEvent: Flow<NavigationEvent>,
) {
    val screenNavigator = screenControllers.screenNavigator
    val modalBottomSheetViewState = appViewState.modalBottomSheetViewState
    val dialog = appViewState.alertViewState
    val globalOverlay = appViewState.globalOverlayViewState
    val backHandlerEnabled = appViewState.backHandlerEnabled
    val onBack = appViewState.onBack

    BackHandler(
        enabled = backHandlerEnabled,
        onBack = onBack,
    )

    NavigationEventHandler(
        screenNavigator,
        navigationEvent,
    )

    NavigationScreens(
        render,
        screenControllers,
        modifier = Modifier.fillMaxSize(),
    )

    ModalBottomSheet(render, modalBottomSheetViewState)

    if (dialog != null) {
        AlertDialog(
            render,
            dialog,
        )
    }

    if (globalOverlay != GlobalOverlayViewState.None) {
        GlobalOverlay(render, globalOverlay)
    }
}

@Composable
private fun BottomSheetLayout(
    render: Render,
    screenControllers: ScreenControllers,
    bottomSheetViewState: BottomSheetViewState?,
    parentScrimColor: Color?,
) {
    val screenManager = screenControllers.screenManager

    val bottomSheetScaffoldState = screenControllers.bottomSheetManager.bottomSheetScaffoldState
    val bottomSheetDescriptor = screenControllers.bottomSheetDescriptor
    val bottomSheetOnUpdate = screenControllers.bottomSheetManager.bottomSheetOnUpdate
    val sheetPeekHeight = bottomSheetDescriptor.peekHeight + bottomSheetDescriptor.peekOffset
    val sheetExpandedOffset = bottomSheetDescriptor.expandedOffset
    val bottomSheetScreenViewState = bottomSheetViewState?.screenViewState
    val bottomSheetScrimColor = bottomSheetViewState?.scrimColor?.composeColor

    val combinedScrimColor = getCombinedScrimColor(
        modalScrimColor = parentScrimColor,
        persistentScrimColor = bottomSheetScrimColor,
    )
    screenManager.onScrimRendered(combinedScrimColor?.color)

    BottomSheetScaffold(
        sheetContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                if (bottomSheetScreenViewState != null) {
                    AppScreen(
                        render,
                        screenViewState = bottomSheetScreenViewState,
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                }
            }
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = sheetPeekHeight,
        sheetExpandedOffset = sheetExpandedOffset,
        sheetShape = CutCornerShape(topStart = 24.dp, topEnd = 0.dp),
    ) {
        NavigationScreens(render, screenControllers, bottomSheetScrimColor, bottomSheetOnUpdate)
    }
}

@Composable
fun NavigationScreens(
    render: Render,
    screenControllers: ScreenControllers,
    bottomSheetScrimColor: Color?,
    bottomSheetOnUpdate: () -> Unit,
) {
    bottomSheetOnUpdate.invoke()

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationScreens(
            render,
            screenControllers,
            modifier = Modifier.fillMaxSize(),
        )

        if (bottomSheetScrimColor != null) {
            Surface(
                color = bottomSheetScrimColor,
                modifier = Modifier.fillMaxSize(),
            ) {}
        }
    }
}


fun getCombinedScrimColor(modalScrimColor: Color?, persistentScrimColor: Color?): Color? {
    if (modalScrimColor == null && persistentScrimColor == null) return null
    if (modalScrimColor == null && persistentScrimColor != null) return persistentScrimColor
    if (modalScrimColor != null && persistentScrimColor == null) return modalScrimColor

    // Ensure both colors have the same RGB components
    if (modalScrimColor!!.red != persistentScrimColor!!.red
        || modalScrimColor.green != persistentScrimColor.green
        || modalScrimColor.blue != persistentScrimColor.blue
    ) {
        return null
    }

    // Calculate the combined alpha
    val combinedAlpha = 1 - (1 - modalScrimColor.alpha) * (1 - persistentScrimColor.alpha)

    // Return the combined color
    return Color(
        red = modalScrimColor.red,
        green = modalScrimColor.green,
        blue = modalScrimColor.blue,
        alpha = combinedAlpha
    )
}