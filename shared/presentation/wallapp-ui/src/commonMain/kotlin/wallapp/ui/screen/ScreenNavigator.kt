package wallapp.ui.screen

import kotlinx.coroutines.flow.StateFlow
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument

interface ScreenNavigator {

    val isReady: StateFlow<Boolean>

    val currentScreen: StateFlow<Screen?>

    fun navigateTo(screenArgument: ScreenArgument, clearScreenStack: Boolean = false)

    fun popBackStack(): Boolean
}
