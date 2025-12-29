package wallapp.ui.screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.system.toast.ToastDisplayController

class ScreenNavigatorLogger(
    private val toastDisplayController: ToastDisplayController,
) : ScreenNavigator {

    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)
    override val currentScreen: StateFlow<Screen?> = MutableStateFlow(null)

    override fun navigateTo(screenArgument: ScreenArgument, clearScreenStack: Boolean) {
        toastDisplayController.showToast("ScreenNavigatorLogger.navigateTo(screenArgument = $screenArgument, clearScreenStack = $clearScreenStack)")
    }

    override fun popBackStack(): Boolean {
        toastDisplayController.showToast("ScreenNavigatorLogger.popBackStack()")
        return false
    }

}