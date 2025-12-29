package wallapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import wallapp.app.AppUiState
import wallapp.bottomsheet.BottomSheetManagerCreateListener
import wallapp.di.resolveDependency
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.bottomsheet.BottomSheetManagerCompose
import wallapp.pixel.bottomsheet3.rememberBottomSheetScaffoldState
import wallapp.pixel.navigation.NavigationManager
import wallapp.screen.Screen
import wallapp.screen.ScreenManager
import wallapp.ui.screen.ScreenNavigator
import wallapp.ui.screen.ScreenNavigatorAndroid
import wallapp.view.ViewSpecFactory
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelProviderFactory

@Composable
fun WallAppNavigation(
    appUiState: AppUiState,
    content: @Composable (ScreenControllers) -> Unit,
) {
    val navigationManager: NavigationManager = resolveDependency()
    val screenManager: ScreenManager = resolveDependency()
    val viewModelFactory: ViewModelFactory = resolveDependency()
    val viewModelProviderFactory: ViewModelProviderFactory = resolveDependency()
    val viewSpecFactory: ViewSpecFactory = resolveDependency()

    val bottomSheetDescriptor: BottomSheetDescriptor = viewSpecFactory.bottomSheetSpec

    WallAppNavigation(
        navigationManager = navigationManager,
        viewModelFactory = viewModelFactory,
        viewModelProviderFactory = viewModelProviderFactory,
        screenManager = screenManager,
        initialScreen = appUiState.initialScreen,
        bottomSheetDescriptor = bottomSheetDescriptor,
        bottomSheetManagerCreateListener = appUiState.bottomSheetManagerCreateListener,
        content = content,
    )
}

@Composable
fun WallAppNavigation(
    navigationManager: NavigationManager,
    viewModelFactory: ViewModelFactory,
    viewModelProviderFactory: ViewModelProviderFactory,
    screenManager: ScreenManager,
    initialScreen: Screen,
    bottomSheetDescriptor: BottomSheetDescriptor,
    bottomSheetManagerCreateListener: BottomSheetManagerCreateListener?,
    content: @Composable (ScreenControllers) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val screenNavHostController: NavHostController = rememberNavController()
    val screenNavigator: ScreenNavigator = remember {
        ScreenNavigatorAndroid(screenNavHostController, coroutineScope)
    }
    LaunchedEffect(screenNavigator) {
        coroutineScope.launch {
            screenNavigator.currentScreen.collect { screen ->
                navigationManager.updateCurrentRouteFormat(screen?.routeFormat)
            }
        }
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val bottomSheetManager = BottomSheetManagerCompose(
        descriptor = bottomSheetDescriptor,
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        coroutineScope = coroutineScope,
    ).also {
        bottomSheetManagerCreateListener?.onBottomSheetManagerCreate(it)
    }

    val screenControllers = ScreenControllersAndroid(
        viewModelFactory,
        viewModelProviderFactory,
        screenNavigator = screenNavigator,
        screenManager = screenManager,
        screenNavHostController = screenNavHostController,
        initialScreen = initialScreen,
        bottomSheetManager = bottomSheetManager,
        bottomSheetDescriptor,
    )

    content.invoke(screenControllers)
}