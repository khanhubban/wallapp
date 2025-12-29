package wallapp.theme

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.appconfig.AppConfig
import wallapp.log.Log
import wallapp.preferences.UserPreferences
import wallapp.resources.string.StringRepository


class ThemeManagerDefault(
    private val systemTheme: SystemTheme,
    private val userPreferences: UserPreferences,
    strings: StringRepository,
    private val appConfig: AppConfig,
    scopeMain: CoroutineScope,
) : ThemeManager {

    private val isCurrentThemeDark: Boolean
        get() {
            return if (themeType.value == ThemeType.System) {
                systemTheme.isDarkTheme
            } else {
                theme.value.isDark
            }
        }

    private val useAltThemes: StateFlow<Boolean> = MutableStateFlow(false)

    private val themeLight: StateFlow<Theme> = useAltThemes.map { useAltThemes ->
        if (useAltThemes) {
            Themes.LightAlt
        } else {
            Themes.Light
        }
    }.stateIn(
        scopeMain,
        started = SharingStarted.Eagerly,
        initialValue = Themes.Light,
    )
    override val lightTheme: Theme
        get() = themeLight.value

    private val themeDark: StateFlow<Theme> = useAltThemes.map { useAltThemes ->
        if (useAltThemes) {
            Themes.DarkAlt
        } else {
            Themes.Dark
        }
    }.stateIn(
        scopeMain,
        started = SharingStarted.Eagerly,
        initialValue = Themes.Dark,
    )
    override val darkTheme: Theme
        get() = themeDark.value
    override val blackTheme: Theme
        get() = Themes.DarkAlt

    private fun arbitrateTheme(
        userThemeType: ThemeType = themeType.value,
        isSystemThemeDark: Boolean = systemTheme.darkTheme.value,
    ): Theme =
        when (userThemeType) {
            ThemeType.System -> {
                if (isSystemThemeDark) {
                    darkTheme
                } else {
                    lightTheme
                }
            }
            ThemeType.Dark -> {
                darkTheme
            }
            else -> {
                lightTheme
            }
        }

    override val themeType: MutableStateFlow<ThemeType>
        get() = userPreferences.themeType

    @FlowInterop.Enabled
    override val theme = combine(
        themeType,
        systemTheme.darkTheme,
        useAltThemes,
    ) { userTheme, isSystemThemeDark, _ ->
        arbitrateTheme(userTheme, isSystemThemeDark)
    }.stateIn(
        scopeMain,
        started = SharingStarted.Eagerly,
        initialValue = arbitrateTheme(),
    )

    private val Theme.oppositeTheme: Theme
        get() = if (isDark) {
            themeLight.value
        } else {
            themeDark.value
        }

    override val oppositeTheme: StateFlow<Theme> = theme
        .map { it.oppositeTheme }
        .stateIn(
            scopeMain,
            started = SharingStarted.Eagerly,
            initialValue = theme.value.oppositeTheme,
        )

    override val allThemeTypes: List<ThemeType> by lazy {
        listOf(
            ThemeType.System,
            ThemeType.Light,
            ThemeType.Dark,
        )
    }

    private val themeTypeSpecDark by lazy { ThemeTypeSpec(label = strings.dark) }
    private val themeTypeSpecLight by lazy { ThemeTypeSpec(label = strings.light) }
    private val themeTypeSpecSystem by lazy { ThemeTypeSpec(label = strings.system) }

    override val themeTypeSpecMap by lazy {
        mapOf(
            ThemeType.System to themeTypeSpecSystem,
            ThemeType.Light to themeTypeSpecLight,
            ThemeType.Dark to themeTypeSpecDark,
        )
    }

    override fun setThemeType(themeType: ThemeType) {
        Log.d("setCurrentTheme() $themeType")
        userPreferences.themeType.value = themeType
    }
}