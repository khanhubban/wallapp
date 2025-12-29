package wallapp.screen

import wallapp.graphics.Color


sealed interface ScreenSystemBarController {

    data class Dynamic(val color: Color) : ScreenSystemBarController

    data object Default : ScreenSystemBarController

    data object DefaultOpposite : ScreenSystemBarController

    data class TranslucentStatusBar(
        val darkStatusBarIcons: Boolean? = null,
    ) : ScreenSystemBarController

    data object BlackStatusBar : ScreenSystemBarController
}
