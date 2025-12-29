package wallapp.screen

import androidx.compose.ui.graphics.compositeOver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import wallapp.coroutine.collectIn
import wallapp.graphics.Color
import wallapp.graphics.color
import wallapp.graphics.composeColor
import wallapp.log.Log
import wallapp.pixel.theme.ThemeColors
import wallapp.screen.ScreenSystemBarController.BlackStatusBar
import wallapp.screen.ScreenSystemBarController.TranslucentStatusBar
import wallapp.system.window.WindowManager
import wallapp.theme.ThemeManager
import wallapp.util.WeakReference

class ScreenManagerDefault(
    private val windowManager: WindowManager,
    private val themeManager: ThemeManager,
    private val coroutineScopeMain: CoroutineScope,
) : ScreenManager {

    override var isSystemInDarkTheme: Boolean? = null
        set(value) = if (field != value) {
            field = value
            updateSystemBarColors()
        } else Unit

    private val currentThemeColors: ThemeColors
        get() = themeManager.theme.value.themeColors
    private val currentInverseThemeColors: ThemeColors
        get() = themeManager.oppositeTheme.value.themeColors

    private var currentScreen: Screen? = null
    override fun onScreenChanged(screen: Screen) {
        this.currentScreen = screen
        Log.d("Current screen: $screen, viewModel: ${screen.controller?.let { it::class.simpleName }}, ${screen.controller?.hashCode()}")
        updateSystemBarColors()
    }

    private val Screen.controller: ScreenSystemBarController?
        get() = recentControllerMap[this]?.get()?.value

    private val recentControllerMap: MutableMap<Screen, WeakReference<StateFlow<ScreenSystemBarController>>> = mutableMapOf()

    private var currentJob: Job? = null

    override fun registerController(screen: Screen, controller: StateFlow<ScreenSystemBarController>) {
        Log.d("Registering Controller for screen: $screen, ${controller::class.simpleName}, ${controller.hashCode()}")
        recentControllerMap[screen] = WeakReference(controller)
        updateSystemBarColors()
        currentJob?.cancel()
        currentJob = controller.collectIn(coroutineScopeMain) {
            updateSystemBarColors()
        }
    }

    private fun updateSystemBarColors() {
        val currentScreen = currentScreen
        currentScreen?.controller?.also { controller ->
            when (controller) {
                is ScreenSystemBarController.Dynamic -> {
                    setStatusBarColor(controller.color)
                }
                is TranslucentStatusBar -> {
                    setStatusBarColorTransparent(darkStatusBarIcons = controller.darkStatusBarIcons)
                }
                BlackStatusBar -> {
                    setStatusBarColor(Color.Black)
                }
                ScreenSystemBarController.Default -> {
                    setStatusBarColorDefault()
                }
                ScreenSystemBarController.DefaultOpposite -> {
                    setStatusBarColorDefaultInverted()
                }
            }
            setNavigationBarColorDefault()
        }
    }

    private fun setNavigationBarColorDefault() = setNavigationBarColor(Color.Transparent)

    private fun setStatusBarColorDefault() = setStatusBarColor(currentThemeColors.background)

    private fun setStatusBarColorDefaultInverted() = setStatusBarColor(
        currentInverseThemeColors.background,
        resetDarkIconUsage = true,
    )

    private fun setStatusBarColorTransparent(darkStatusBarIcons: Boolean?) {
        setStatusBarColor(Color.Transparent, resetDarkIconUsage = darkStatusBarIcons == null)
        if (darkStatusBarIcons != null) {
            windowManager.setStatusBarDarkIcons(darkStatusBarIcons)
        }
    }

    private var screenStatusBarColor: Color? = null
    private fun setStatusBarColor(color: Color, resetDarkIconUsage: Boolean = true) {
        screenStatusBarColor = color
        updateStatusBarColor(color, scrimColor, resetDarkIconUsage)
    }

    private var scrimColor: Color? = null
    override fun onScrimRendered(scrimColor: Color?) {
        this.scrimColor = scrimColor
        val screenStatusBarColor = screenStatusBarColor ?: return
        updateStatusBarColor(screenStatusBarColor, scrimColor, false)
    }

    private fun updateStatusBarColor(
        screenStatusBarColor: Color,
        scrimColor: Color?,
        resetDarkIconUsage: Boolean,
    ) {
        if (resetDarkIconUsage) {
            windowManager.setStatusBarDarkIcons(darkIcons = null)
        }

        val combinedColor = if (scrimColor != null) {
            scrimColor.composeColor.compositeOver(screenStatusBarColor.composeColor).color
        } else {
            screenStatusBarColor
        }

        windowManager.setStatusBarColor(combinedColor)
    }

    private fun setNavigationBarColor(color: Color) = windowManager.setNavigationBarColor(color)
    
    init {
        themeManager.theme.collectIn(coroutineScopeMain) {
            updateSystemBarColors()
        }
    }
}