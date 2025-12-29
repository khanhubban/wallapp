package wallapp.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.pixel.navigation.NavigationManager
import wallapp.screen.Screen

class CurrentScreenProviderDefault(
    navigationManager: NavigationManager,
    coroutineScopeMain: CoroutineScope
) : CurrentScreenProvider {

    override val currentScreenFlow: StateFlow<Screen?> = navigationManager.currentRouteFormat
        .map { route -> route?.let { Screen.fromRoute(it) } }
        .stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = null)

    override fun currentScreen(): Screen? = currentScreenFlow.value
}