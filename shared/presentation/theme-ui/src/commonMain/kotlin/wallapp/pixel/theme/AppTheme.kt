package wallapp.pixel.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import wallapp.pixel.render.Render
import wallapp.system.window.SystemBarColors

val LocalSystemBarColors = compositionLocalOf<SystemBarColors> {
    error("No SystemBarColors provided")
}

@Composable
fun AppTheme(
    render: Render,
    systemBarColors: SystemBarColors,
    colorScheme: ColorScheme,
    typography: Typography,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSystemBarColors provides systemBarColors) {
        SystemUiTheme(systemBarColors = systemBarColors)

        AppTheme(
            render = render,
            colorScheme = colorScheme,
            typography = typography,
        ) {
            content.invoke()
        }
    }
}

@Composable
fun AppTheme(
    render: Render,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    canApplyInsetDecorations: Boolean = true,
    content: @Composable () -> Unit,
) {
    val systemBarColors = LocalSystemBarColors.current

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = typography,
    ) {
        AppTheme(systemBarColors, canApplyInsetDecorations, content)
    }
}

@Composable
private fun AppTheme(
    systemBarColors: SystemBarColors,
    canApplyInsetDecorations: Boolean = true,
    content: @Composable () -> Unit,
) {
    AppThemeNoSystemBars(content)
}

@Composable
private fun AppThemeNoSystemBars(
    content: @Composable () -> Unit,
) {
    content.invoke()
}

//@Composable
//private fun AppThemeSystemBars(
//    systemBarColors: SystemBarColors,
//    windowFrame: WindowFrame,
//    content: @Composable () -> Unit,
//) {
//    val statusBarColor = systemBarColors.statusBarColor.composeColor
//    val navigationBarColor = systemBarColors.navigationBarColor.composeColor
//    val statusBarHeight = windowFrame.statusBarHeight
//    val navigationBarHeight = windowFrame.navigationBarHeight
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        content.invoke()
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(statusBarHeight)
//                .background(statusBarColor)
//                .align(Alignment.TopCenter)
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(navigationBarHeight)
//                .background(navigationBarColor)
//                .align(Alignment.BottomCenter),
//        )
//    }
//}