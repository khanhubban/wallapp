package wallapp.content.state.colors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.coroutine.CoroutineScopeMain
import wallapp.graphics.Color
import wallapp.theme.Theme
import wallapp.theme.ThemeManager
import wallapp.theme.Themes
import wallapp.theme.Themes.DarkSurfaceVariantAlpha
import wallapp.theme.Themes.LightSurfaceVariantAlpha

class ContentColorManagerDefault(
    private val themeManager: ThemeManager,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
) : ContentColorManager {

    override val topBarContainer: StateFlow<Color> by lazy {
        themeManager.theme
            .map { it.themeColors.surfaceVariant }
            .stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = themeManager.theme.value.themeColors.surfaceVariant,
            )
    }

    override val profileTopBarContainer: StateFlow<Color>
        get() = indexBottomBarContainer

    private val Theme.backgroundVariant: Color
        get() {
            val alpha = if (isDark) {
                DarkSurfaceVariantAlpha
            } else {
                LightSurfaceVariantAlpha
            }
            return themeColors.background.copy(alpha = alpha)
        }

    private val backgroundVariant: StateFlow<Color> by lazy {
        themeManager.theme
            .map { it.backgroundVariant }
            .stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = themeManager.theme.value.backgroundVariant,
            )
    }

    /**
     * Hack to ensure that bottom bar always uses the background from [Themes.Light] or
     * [Themes.Dark], not the background from [Themes.LightAlt] or [Themes.DarkAlt].
     */
    private val Theme.indexBottomBarContainer: Color
        get() {
            return if (isDark) {
                Themes.Dark.themeColors.background.copy(alpha = DarkSurfaceVariantAlpha)
            } else {
                Themes.Light.themeColors.background.copy(alpha = LightSurfaceVariantAlpha)
            }
        }

    override val indexBottomBarContainer: StateFlow<Color> by lazy {
        themeManager.theme.map { it.indexBottomBarContainer }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = themeManager.theme.value.indexBottomBarContainer,
        )
    }

    override val indexStatusBarContainer: StateFlow<Color>
        get() = indexBottomBarContainer

    override val controlButtonBackgroundColorDark: StateFlow<Color> =
        MutableStateFlow(Themes.Dark.controlButtonBackgroundColor)
    override val controlButtonBackgroundColorLight: StateFlow<Color> =
        MutableStateFlow(Themes.Light.controlButtonBackgroundColor)
    override val controlButtonOnBackgroundColorDark: StateFlow<Color> =
        MutableStateFlow(Themes.Dark.controlButtonOnBackgroundColor)
    override val controlButtonOnBackgroundColorLight: StateFlow<Color> =
        MutableStateFlow(Themes.Light.controlButtonOnBackgroundColor)

    private val Theme.controlButtonBackgroundColor: Color
        get() {
            val alpha = if (isDark) {
                .8f
            } else {
                .8f
            }
            return themeColors.surface.copy(alpha = alpha)
        }
    private val Theme.controlButtonOnBackgroundColor: Color
        get() = themeColors.onSurface!!.color
}