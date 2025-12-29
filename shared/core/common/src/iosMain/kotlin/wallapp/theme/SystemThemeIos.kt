package wallapp.theme

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.log.Log

class SystemThemeIos : SystemTheme {
    override val darkTheme: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isDarkTheme: Boolean
        get() = darkTheme.value

    fun setIsDarkTheme() {
        setIsDarkTheme(isDarkTheme = true)
    }

    fun setIsLightTheme() {
        setIsDarkTheme(isDarkTheme = false)
    }

    fun setIsDarkTheme(isDarkTheme: Boolean) {
        Log.d("setIsDarkTheme(isDarkTheme: $isDarkTheme)")
        darkTheme.value = isDarkTheme
    }
}