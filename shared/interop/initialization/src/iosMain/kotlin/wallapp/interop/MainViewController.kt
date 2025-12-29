package wallapp.interop

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import wallapp.image.loader.ImageLoader
import wallapp.log.Log
import wallapp.pixel.view.SingleView
import wallapp.screen.ScreenArgument
import wallapp.screen.screen
import wallapp.ui.app.WallAppCompositionLocalProvider
import wallapp.ui.app.WallAppContent
import wallapp.ui.app.WallAppContentSingleScreen
import wallapp.ui.app.WallAppContentSingleView
import wallapp.ui.precompose.WaePreComposeApplication

fun MainViewController(imageLoader: ImageLoader, screenArgument: ScreenArgument?): UIViewController {
    Log.d("[Lifecycle] create MainViewController()")

    return WaePreComposeApplication {
        WallAppCompositionLocalProvider(imageLoader) {
            if (screenArgument != null && screenArgument.screen.nativeNavigationSupported) {
                WallAppContentSingleScreen(screenArgument)
            } else {
                WallAppContent(Modifier, MvpApp.createAppUiState())
            }
        }
    }
}

@OptIn(ExperimentalComposeApi::class)
fun ComposeViewController(imageLoader: ImageLoader, singleView: SingleView): UIViewController {
    Log.d("[Lifecycle] create ComposeViewController()")

    return ComposeUIViewController(configure = { opaque = false }) {
        WallAppCompositionLocalProvider(imageLoader) {
            WallAppContentSingleView(singleView)
        }
    }
}