package wallapp.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import wallapp.pixel.render.Render
import wallapp.screen.Screen
import wallapp.screen.screenTransition

val Screen.arguments: List<NamedNavArgument>
    get() {
        val argument = stringArgumentName
        if (argument != null) {
            return listOf(navArgument(argument) { type = NavType.StringType })
        }

        return emptyList()
    }

fun Screen.getArgument(backStackEntry: NavBackStackEntry): String? =
    backStackEntry.arguments?.getString(stringArgumentName)

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composable(
    render: Render,
    screen: Screen,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(String?) -> Unit,
) {
    val screenTransition = screen.screenTransition(render)
    composable(
        route = screen.routeFormat,
        arguments = screen.arguments,
        deepLinks = deepLinks,
        enterTransition = screenTransition.enterTransition,
        exitTransition = screenTransition.exitTransition,
        popEnterTransition = screenTransition.popEnterTransition,
        popExitTransition = screenTransition.popExitTransition,
    ) { navBackStackEntry ->
        content.invoke(this, screen.getArgument(navBackStackEntry))
    }
}

fun NavHostController.navigate(screen: Screen, argument: String?) {
    navigate(screen.routeBuilder.invoke(argument))
}

val NavHostController.currentScreen: Screen?
    get() = currentDestination?.route?.let { Screen.fromRoute(it) }