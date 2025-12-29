package wallapp

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import moe.tlaster.precompose.PreComposeApp
import wallapp.di.module.AllModules
import wallapp.interop.MvpApp
import wallapp.interop.MvpAppDesktop
import wallapp.ui.app.WallAppCompositionLocalProvider
import wallapp.ui.app.WallAppContent
import androidx.compose.ui.unit.Dp as ComposeDp

// Temp, unchecked function to get us going.
fun ComposeDp.toPx(): Float {
    val dpi = 96    // Standard DPI for desktop screens
    return this.value * (dpi / 160.0f) // 160 is the baseline density which is the same as Android.
}

/**
 * Desktop application entry point.
 *
 * ⚠️ Do not use the green Run/Debug gutter button on this function.
 * Resources will not be generated correctly and the app may crash.
 *
 * ✅ Instead, run using the Gradle task:
 *    ./gradlew :app:desktop:run
 *
 * Or configure an IDE Run/Debug configuration that executes the Gradle
 * run task. This ensures resources are available and the app behaves correctly.
 */
fun main() {
    val interopModules = MvpAppDesktop(AllModules)

    val windowInsetsManagerDefault = interopModules.windowFrameManagerDefault
    val imageLoader = interopModules.imageLoader

    val appUiState = MvpApp.createAppUiState()

    application {
        val windowState = rememberWindowState(
            size = DpSize(400.dp, 800.dp)
        )

        windowInsetsManagerDefault.setInsets(
            statusBarHeight = 0.dp,
            navBarHeight = 0.dp,
            deviceWidthPx = windowState.size.width.toPx().toInt(),
            deviceHeightPx = windowState.size.height.toPx().toInt(),
        )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "WallApp Demo",
        ) {
            PreComposeApp {
                WallAppCompositionLocalProvider(imageLoader) {
                    WallAppContent(Modifier, appUiState)
                }
            }
        }
    }
}