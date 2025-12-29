package wallapp.navigation

import kotlinx.coroutines.flow.StateFlow
import wallapp.screen.Screen

interface CurrentScreenProvider {

    val currentScreenFlow: StateFlow<Screen?>
    fun currentScreen(): Screen?
}

interface CurrentScreenCoordinatorIos {
    fun currentScreen(): Screen?
}