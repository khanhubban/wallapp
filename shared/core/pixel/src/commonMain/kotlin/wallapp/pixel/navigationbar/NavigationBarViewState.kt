package wallapp.pixel.navigationbar

import androidx.compose.runtime.Immutable
import wallapp.pixel.util.ColorOptional
import wallapp.pixel.view.ViewState

@Immutable
data class NavigationBarViewState(
    val viewSpec: NavigationBarViewSpec,
    val items: List<NavigationBarItem>,
    val itemsDisplayRipple: Boolean = true,
    val containerColor: ColorOptional? = null,
    val offsetProgress: Float = 0f,
    /**
     * [true]: always show
     * [false]: always hide
     * [null]: show/hide based on scroll direction
     */
    val forceShow: Boolean? = null,
): ViewState
