package wallapp.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.screen.Screen

class CurrentScreenProviderIos(
    private val currentScreenCoordinatorIos: CurrentScreenCoordinatorIos
) : CurrentScreenProvider {

    // Hardcoding Index here is fine for now as the usage for this Flow doesn't affect iOS
    // This was introduced to make sure we can navigate to error screen which wasn't working
    // on Android as the error navigation event was being overwritten by a new one
    override val currentScreenFlow: StateFlow<Screen?> = MutableStateFlow(Screen.Index)

    override fun currentScreen(): Screen? {
        return currentScreenCoordinatorIos.currentScreen()
    }
}