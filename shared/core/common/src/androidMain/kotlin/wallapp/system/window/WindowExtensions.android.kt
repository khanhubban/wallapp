package wallapp.system.window

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.WindowInsets
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import wallapp.system.unit.SystemUnitManager
import android.view.WindowManager as WindowManagerSystem

fun Activity.statusBarHeight(systemUnitManager: SystemUnitManager): Dp {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowManager.statusBarHeightApi30(systemUnitManager)
    } else {
        val insets = ViewCompat.getRootWindowInsets(window.decorView)
            ?.getInsets(WindowInsetsCompat.Type.statusBars())
        insets?.top?.let { systemUnitManager.pxToDp(it) }
            ?: resources.getSystemStatusBarHeightLegacy(systemUnitManager)
    }
}

private fun WindowManagerSystem.statusBarHeightApi30(systemUnitManager: SystemUnitManager): Dp {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            currentWindowMetrics
            .windowInsets
            .getInsets(WindowInsets.Type.statusBars())
            .top
            .let { systemUnitManager.pxToDp(it) }
    } else {
        TODO("Not supported on pre-Android 11.")
    }
}

private fun Resources.getSystemStatusBarHeightLegacy(systemUnitManager: SystemUnitManager): Dp {
    val resourceId = getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) {
        val heightPx = getDimensionPixelSize(resourceId)
        systemUnitManager.pxToDp(heightPx)
    } else {
        24.dp
    }
}

fun Activity.navigationBarHeight(systemUnitManager: SystemUnitManager): Dp {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowManager.navigationBarHeightApi30(systemUnitManager)
    } else {
        val insets = ViewCompat.getRootWindowInsets(window.decorView)
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
        insets?.bottom?.let { systemUnitManager.pxToDp(it) }
            ?: resources.getSystemNavBarHeightLegacy(systemUnitManager)
    }
}

private fun WindowManagerSystem.navigationBarHeightApi30(systemUnitManager: SystemUnitManager): Dp {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            currentWindowMetrics
            .windowInsets
            .getInsets(WindowInsets.Type.navigationBars())
            .bottom
            .let { systemUnitManager.pxToDp(it) }
    } else {
        TODO("Not supported on pre-Android 11.")
    }
}

private fun Resources.getSystemNavBarHeightLegacy(systemUnitManager: SystemUnitManager): Dp {
    val resourceId = getIdentifier("navigation_bar_height", "dimen", "android")

    return if (resourceId > 0) {
        val heightPx = getDimensionPixelSize(resourceId)
        systemUnitManager.pxToDp(heightPx)
    } else {
        48.dp
    }
}

/**
 * Returns the device size in pixels (status / nav bar inclusive).
 */
fun WindowManagerSystem.deviceSizePx(context: Context): Pair<Int, Int> {
    val deviceWidthPx: Int
    val deviceHeightPx: Int
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = currentWindowMetrics

        deviceWidthPx = windowMetrics.bounds.width()
        deviceHeightPx = windowMetrics.bounds.height()
    } else {
        val metrics = context.resources.displayMetrics
        deviceWidthPx = metrics.widthPixels
        deviceHeightPx = metrics.heightPixels
    }
    return deviceWidthPx to deviceHeightPx
}