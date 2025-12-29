package wallapp.ui.screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument

object ScreenNavigatorNoOp : ScreenNavigator {

    override val isReady = MutableStateFlow(false)

    override val currentScreen: StateFlow<Screen?> = MutableStateFlow(null)

    override fun navigateTo(screenArgument: ScreenArgument, clearScreenStack: Boolean) { }

    override fun popBackStack(): Boolean = false
}