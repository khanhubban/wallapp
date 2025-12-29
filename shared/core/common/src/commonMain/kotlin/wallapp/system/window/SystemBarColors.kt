package wallapp.system.window

import wallapp.graphics.Color

data class SystemBarColors(
    val statusBarColor: Color,
    val darkStatusBarIcons: Boolean? = null,
    val navigationBarColor: Color,
) {

    companion object {
        val Preset = SystemBarColors(
            statusBarColor = Color.Transparent,
            navigationBarColor = Color.Transparent,
        )
    }

}
