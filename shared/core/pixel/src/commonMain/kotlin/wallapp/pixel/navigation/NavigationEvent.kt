package wallapp.pixel.navigation

import co.touchlab.skie.configuration.annotations.SealedInterop

@SealedInterop.Enabled
sealed class NavigationEvent {

    data class ToScreenEvent(
        val argument: NavigationArgument,
        val clearScreenStack: Boolean = false,
    ) : NavigationEvent()

    data object PopScreenEvent : NavigationEvent()

    data object NoOpEvent : NavigationEvent()
}