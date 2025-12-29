package wallapp.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.bottomsheet.BottomSheetManagerCreateListener
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.Screen
import wallapp.theme.Theme

interface AppUiState {

    val appViewState: StateFlow<AppViewState>

    val navigationEvent: Flow<NavigationEvent>

    val theme: StateFlow<Theme>

    val bottomSheetManagerCreateListener: BottomSheetManagerCreateListener

    val initialScreen: Screen

    val enableIosNativeNavigation: StateFlow<Boolean>
}