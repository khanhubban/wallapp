package wallapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import wallapp.log.Log
import wallapp.pixel.navigation.NavigationEvent
import wallapp.screen.ScreenArgument
import wallapp.ui.screen.ScreenNavigator

@Composable
fun NavigationEventHandler(
    screenNavigator: ScreenNavigator,
    navigationEvent: Flow<NavigationEvent>,
) {
    LaunchedEffect(navigationEvent) {
        navigationEvent.collect { event ->
            onNavigationEvent(
                screenNavigator = screenNavigator,
                navigationEvent = event,
            )
        }
    }

//    ObserveAsEvents(navigationEvent) { event: NavigationEvent ->
//        onNavigationEvent(
//            screenNavigator = screenNavigator,
//            navigationEvent = event,
//        )
//    }
}

fun onNavigationEvent(
    screenNavigator: ScreenNavigator,
    navigationEvent: NavigationEvent,
) {
    Log.d("[Navigation] onNavigationEvent() event: $navigationEvent")
    when (navigationEvent) {
        is NavigationEvent.ToScreenEvent -> {
            screenNavigator.navigateTo(
                screenArgument = navigationEvent.argument as ScreenArgument,
                clearScreenStack = navigationEvent.clearScreenStack,
            )
        }

        is NavigationEvent.PopScreenEvent -> {
            screenNavigator.popBackStack()
        }

        is NavigationEvent.NoOpEvent -> { }
    }
}