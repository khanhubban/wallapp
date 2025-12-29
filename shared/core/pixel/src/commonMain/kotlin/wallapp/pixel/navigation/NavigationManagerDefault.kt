package wallapp.pixel.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.log.Log
import wallapp.string.quote

class NavigationManagerDefault() : NavigationManager {

    override suspend fun sendNavigationEvent(event: NavigationEvent) {
        Log.d("[Navigation] sendNavigationEvent(): $event")
        _navigationEvent.emit(event)
    }

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    override val navigationEvent: Flow<NavigationEvent>
        get() = _navigationEvent

    override val currentRouteFormat: MutableStateFlow<String?> = MutableStateFlow(null)
    override fun updateCurrentRouteFormat(route: String?) {
        currentRouteFormat.value = route
        Log.run { d("[Navigation] updateCurrentRouteFormat(): ${route.quote()}") }
    }
}