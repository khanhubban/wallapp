package wallapp.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.bottomsheet.BottomSheetManagerCreateListener
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.Screen
import wallapp.theme.Theme
import wallapp.theme.ThemeManager

fun AppUiState(
    appViewModel: AppViewModel,
    themeManager: ThemeManager,
): AppUiState = object : AppUiState {
    override val appViewState: StateFlow<AppViewState>
        get() = appViewModel.appViewState
    override val navigationEvent: Flow<NavigationEvent>
        get() = appViewModel.navigationEvent
    override val theme: StateFlow<Theme>
        get() = themeManager.theme
    override val bottomSheetManagerCreateListener: BottomSheetManagerCreateListener
        get() = appViewModel
    override val initialScreen: Screen
        get() = appViewModel.initialScreen

    override val enableIosNativeNavigation: StateFlow<Boolean>
        get() = appViewModel.enableIosNativeNavigation
}
