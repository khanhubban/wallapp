package wallapp.pixel.navigation

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.Flow


interface NavigationManager {

    suspend fun sendNavigationEvent(event: NavigationEvent)

    @FlowInterop.Enabled
    val navigationEvent: Flow<NavigationEvent>

    val currentRouteFormat: Flow<String?>
    fun updateCurrentRouteFormat(route: String?)
}