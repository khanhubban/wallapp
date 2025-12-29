package wallapp.ui.screen

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Log
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.screen.networkErrorScreens
import wallapp.screen.screen


fun NavHostController.navigate(screen: Screen, argument: String?, popUpToScreen: Screen?) {
    navigate(screen.routeBuilder.invoke(argument)) {
        if (popUpToScreen != null) {
            popUpTo(popUpToScreen.routeBuilder.invoke(null)) {
                inclusive = true
            }
        }
    }
}

val NavHostController.currentScreen: Screen?
    get() = currentDestination?.route?.let { Screen.fromRoute(it) }


class ScreenNavigatorAndroid(
    private val navHostController: NavHostController,
    coroutineScope: CoroutineScope,
) : ScreenNavigator {

    override val currentScreen: StateFlow<Screen?> by lazy {
        navHostController.currentBackStackEntryFlow
            .map { entry -> entry.destination.route?.let { Screen.fromRoute(it) } }
            .stateIn(coroutineScope, started = SharingStarted.Eagerly, null)
    }

    override val isReady: StateFlow<Boolean> by lazy {
        currentScreen.map { it != null }
            .stateIn(coroutineScope, started = SharingStarted.Eagerly, false)
    }

    private fun navigateTo(screen: Screen, argument: String?, clearScreenStack: Boolean) {
        val popUpToScreen = if (clearScreenStack) {
            navHostController.currentScreen
        } else {
            null
        }
        Log.i("[Navigation] navigateTo(screen: $screen, argument = $argument, clearScreenStack = $clearScreenStack)")
        navHostController.navigate(screen, argument, popUpToScreen)
//        logScreenStack("navigateTo()")
    }

    override fun navigateTo(screenArgument: ScreenArgument, clearScreenStack: Boolean) {
        if (currentScreen.value in networkErrorScreens && screenArgument.screen in networkErrorScreens) {
            Log.w("[Navigation] *** navigateTo() already on ErrorNetwork screen")
            return
        }
        val screen = screenArgument.screen
        navigateTo(screen, argument = screenArgument.jsonString, clearScreenStack)
    }

    override fun popBackStack(): Boolean {
        Log.i("[Navigation] popBackStack(), currentScreen: ${currentScreen.value}")

        return navHostController.popBackStack()
            .also {
                if (navHostController.currentDestination == null) {
                    // This can happen if the user goes deep into the app and then spams the back
                    // button. The app behaves as expected when it is next opened, so there's no
                    // need to assert here. #2217.
                    Log.w("[Navigation] *** popBackStack() currentDestination is null")
                }
            }
    }

//    fun logScreenStack(via: String) {
//        val stack = navHostController.currentBackStack.value
//        val logSize = stack.size - 1
//        Log.w("[Navigation] logScreenStack() stack size: ${stack.size} via ${via.quote()}:")
//        stack.forEachIndexed { index, it ->
//            Log.i("[Navigation]   [$index]/[${logSize}] route: ${it.destination.route.quote()}")
//        }
//    }

    init {
        Log.w("[Navigation] *** ScreenNavigatorAndroid init()")

//        currentScreen.collectIn(coroutineScope) {
//            logScreenStack("currentScreen.collect")
//        }
    }
}