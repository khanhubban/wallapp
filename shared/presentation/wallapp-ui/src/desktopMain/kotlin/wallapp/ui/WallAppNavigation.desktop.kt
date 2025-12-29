package wallapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.rememberNavigator
import wallapp.app.AppUiState
import wallapp.bottomsheet.BottomSheetManagerCreateListener
import wallapp.di.resolveDependency
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.bottomsheet.BottomSheetManagerCompose
import wallapp.pixel.bottomsheet3.rememberBottomSheetScaffoldState
import wallapp.pixel.navigation.NavigationManager
import wallapp.screen.ScreenManager
import wallapp.ui.screen.ScreenNavigatorPrecompose
import wallapp.view.ViewSpecFactory
import wallapp.viewmodel.ViewModelFactory

@Composable
fun WallAppNavigation(
    appUiState: AppUiState,
    content: @Composable (ScreenControllers) -> Unit,
) {
    val navigationManager: NavigationManager = resolveDependency()
    val screenManager: ScreenManager = resolveDependency()
    val viewModelFactory: ViewModelFactory = resolveDependency()
    val viewSpecFactory: ViewSpecFactory = resolveDependency()

    val bottomSheetDescriptor: BottomSheetDescriptor = viewSpecFactory.bottomSheetSpec

    WallAppNavigation(
        appUiState,
        navigationManager = navigationManager,
        viewModelFactory = viewModelFactory,
        screenManager = screenManager,
        bottomSheetDescriptor = bottomSheetDescriptor,
        bottomSheetManagerCreateListener = appUiState.bottomSheetManagerCreateListener,
        content = content,
    )
}

@Composable
private fun WallAppNavigation(
    appUiState: AppUiState,
    navigationManager: NavigationManager,
    viewModelFactory: ViewModelFactory,
    screenManager: ScreenManager,
    bottomSheetDescriptor: BottomSheetDescriptor,
    bottomSheetManagerCreateListener: BottomSheetManagerCreateListener?,
    content: @Composable (ScreenControllers) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val navigator = rememberNavigator()
    val screenNavigator = remember {
        ScreenNavigatorPrecompose(
            navigator = navigator,
            coroutineScope = coroutineScope,
        )
    }
    val initialScreen = remember(screenNavigator) {
        appUiState.initialScreen
    }
    LaunchedEffect(screenNavigator) {
        coroutineScope.launch {
            screenNavigator.currentScreen.collect { screen ->
                navigationManager.updateCurrentRouteFormat(screen?.routeFormat)
            }
        }
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val bottomSheetManager = remember {
        BottomSheetManagerCompose(
            descriptor = bottomSheetDescriptor,
            bottomSheetScaffoldState = bottomSheetScaffoldState,
            coroutineScope = coroutineScope,
        ).also {
            bottomSheetManagerCreateListener?.onBottomSheetManagerCreate(it)
        }
    }

    val screenControllers = ScreenControllersDesktop(
        viewModelFactory,
        screenNavigator,
        screenManager,
        navigator = navigator,
        initialScreen = initialScreen,
        bottomSheetManager = bottomSheetManager,
        bottomSheetDescriptor,
    )

    content(screenControllers)
}