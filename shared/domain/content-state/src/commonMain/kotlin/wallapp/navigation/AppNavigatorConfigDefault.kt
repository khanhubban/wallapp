package wallapp.navigation

import wallapp.screen.Screen

data class AppNavigatorConfigDefault(
    override val appRootScreens: List<Screen> = listOf(Screen.Index, Screen.FirstRun)
) : AppNavigatorConfig