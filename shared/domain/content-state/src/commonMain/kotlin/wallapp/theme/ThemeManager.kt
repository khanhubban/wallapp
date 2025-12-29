package wallapp.theme

import kotlinx.coroutines.flow.StateFlow

interface ThemeManager {

    val darkTheme: Theme
    val lightTheme: Theme
    val blackTheme: Theme

    val theme: StateFlow<Theme>
    val oppositeTheme: StateFlow<Theme>

    val themeType: StateFlow<ThemeType>
    fun setThemeType(themeType: ThemeType)

    val allThemeTypes: List<ThemeType>
    val themeTypeSpecMap: Map<ThemeType, ThemeTypeSpec>
}

fun ThemeManager.getThemeTypeSpec(themeType: ThemeType): ThemeTypeSpec {
    return themeTypeSpecMap[themeType]!!
}