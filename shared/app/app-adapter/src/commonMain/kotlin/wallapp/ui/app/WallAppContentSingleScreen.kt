package wallapp.ui.app

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import wallapp.app.AppUiState
import wallapp.app.AppViewState
import wallapp.bottomsheet.BottomSheetManagerCreateListener
import wallapp.di.resolveDependency
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.bottomsheet.BottomSheetManagerCompose
import wallapp.pixel.globaloverlay.GlobalOverlayViewState
import wallapp.pixel.navigation.NavigationEvent
import wallapp.pixel.render.Render
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.resources.AppTypography
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenManager
import wallapp.screen.createViewModel
import wallapp.theme.Theme
import wallapp.theme.ThemeManager
import wallapp.ui.Render
import wallapp.ui.ScreenControllers
import wallapp.ui.WallAppScreen
import wallapp.ui.screen.ScreenNavigator
import wallapp.ui.screen.ScreenNavigatorLogger
import wallapp.viewmodel.ViewModelFactory

@Composable
fun WallAppContentSingleScreen(
    screenArgument: ScreenArgument,
    modifier: Modifier = Modifier,
    alertViewState: StateFlow<AlertViewState?> = MutableStateFlow(null),
) {
    val viewModelFactory = resolveDependency<ViewModelFactory>()
    val viewModel = viewModel {
        requireNotNull(screenArgument.createViewModel(viewModelFactory) as? ViewModel) {
            "Unsupported argument for single screen: $screenArgument, screen does not support native navigation"
        }
    }
    val screenViewState: StateFlow<ScreenViewState> = (viewModel as ScreenViewStateProvider).viewState
    val navigationEvent: MutableStateFlow<NavigationEvent> =
        MutableStateFlow(NavigationEvent.NoOpEvent)
    WallAppContentSingleScreen(screenViewState, navigationEvent, modifier, alertViewState)
}

@Composable
fun WallAppContentSingleScreen(
    screenViewState: StateFlow<ScreenViewState>,
    navigationEvent: Flow<NavigationEvent>,
    modifier: Modifier = Modifier,
    alertViewState: StateFlow<AlertViewState?> = MutableStateFlow(null),
) {
    val coroutineScope = rememberCoroutineScope()
    val render: Render = remember { Render() }
    val typography: Typography = AppTypography()
    val typography3rdParty = AppTypography()

    val screenControllers = createSingleScreenScreenControllers()
    val appUiState = createSingleScreenAppUiState(screenViewState, navigationEvent, alertViewState, coroutineScope)

    WallAppScreen(
        render,
        screenControllers,
        appUiState,
        typography = typography,
        typography3rdParty = typography3rdParty,
        modifier,
    )
}


fun createSingleScreenScreenControllers(): ScreenControllers {
    return object : ScreenControllers {
        override val viewModelFactory: ViewModelFactory
            get() = TODO("Not implemented for SingleScreen")
        override val screenNavigator: ScreenNavigator by lazy {
            ScreenNavigatorLogger(resolveDependency())
        }
        override val screenManager: ScreenManager by lazy { resolveDependency() }
        override val bottomSheetManager: BottomSheetManagerCompose
            get() = TODO("Not implemented for SingleScreen")
        override val bottomSheetDescriptor: BottomSheetDescriptor
            get() = TODO("Not implemented for SingleScreen")
    }
}

private fun createAppViewState(
    screenViewState: ScreenViewState,
    alertViewState: AlertViewState?,
) = AppViewState(
    screenViewState,
    bottomSheetViewState = null,
    modalBottomSheetViewState = null,
    alertViewState = alertViewState,
    backHandlerEnabled = false,
    globalOverlayViewState = GlobalOverlayViewState.None,
    onBack = { },
)

private fun createAppViewState(
    screenViewState: StateFlow<ScreenViewState>,
    alertViewState: StateFlow<AlertViewState?>,
    coroutineScope: CoroutineScope,
): StateFlow<AppViewState> =
    combine(screenViewState, alertViewState) { screen, alert ->
        createAppViewState(screen, alert)
    }.stateIn(
        coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = createAppViewState(screenViewState.value, alertViewState.value),
    )

fun createSingleScreenAppUiState(
    screenViewState: StateFlow<ScreenViewState>,
    navigationEvent: Flow<NavigationEvent>,
    alertViewState: StateFlow<AlertViewState?>,
    coroutineScope: CoroutineScope,
): AppUiState {
    val themeManager = resolveDependency<ThemeManager>()
    return object : AppUiState {
        override val appViewState: StateFlow<AppViewState>
            get() = createAppViewState(screenViewState, alertViewState, coroutineScope)
        override val navigationEvent: Flow<NavigationEvent>
            get() = navigationEvent
        override val theme: StateFlow<Theme>
            get() = themeManager.theme
        override val bottomSheetManagerCreateListener: BottomSheetManagerCreateListener
            get() = TODO("Not implemented for SingleScreen")
        override val initialScreen: Screen
            get() = TODO("Not implemented for SingleScreen")
        override val enableIosNativeNavigation: StateFlow<Boolean>
            get() = TODO("Not implemented for SingleScreen")
    }
}
