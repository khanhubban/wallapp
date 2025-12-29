package wallapp.screen

import kotlinx.coroutines.flow.StateFlow
import wallapp.graphics.Color

interface ScreenManager {

    var isSystemInDarkTheme: Boolean?

    fun onScreenChanged(screen: Screen)
    fun registerController(screen: Screen, controller: StateFlow<ScreenSystemBarController>)

    fun onScrimRendered(scrimColor: Color?)
}