package wallapp.ui.screen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.screen.screen

class ScreenNavigatorPrecompose(
    private val navigator: Navigator,
    coroutineScope: CoroutineScope,
    private val enableIosNativeNavigation: StateFlow<Boolean> = MutableStateFlow(false),
) : ScreenNavigator {

    private val currentScreenRoute: StateFlow<String?> = navigator.currentEntry
        .map { it?.route?.route }
        .stateIn(coroutineScope, started = SharingStarted.Eagerly, null)

    override val isReady: StateFlow<Boolean> = currentScreenRoute
        .map { it != null }
        .stateIn(coroutineScope, started = SharingStarted.Eagerly, false)

    override val currentScreen: StateFlow<Screen?> =
        currentScreenRoute.map { route -> route?.let { Screen.fromRoute(it) } }
            .stateIn(coroutineScope, started = SharingStarted.Eagerly, null)

    private fun navigateTo(screen: Screen, argument: String?, clearScreenStack: Boolean) {
        val options: NavOptions? = if (clearScreenStack) {
            currentScreenRoute.value?.let {
                NavOptions(popUpTo = PopUpTo(route = it, inclusive = true))
            }
        } else {
            null
        }
        navigator.navigate(screen.routeBuilder.invoke(argument), options)
    }

    override fun navigateTo(screenArgument: ScreenArgument, clearScreenStack: Boolean) {
        val screen = screenArgument.screen
        if (screen.nativeNavigationSupported && enableIosNativeNavigation.value) return
        navigateTo(screen, argument = screenArgument.jsonString, clearScreenStack)
    }

    override fun popBackStack(): Boolean {
        if (currentScreen.value != Screen.Index) {
            navigator.popBackStack()
            return true
        }
        return false
    }
}