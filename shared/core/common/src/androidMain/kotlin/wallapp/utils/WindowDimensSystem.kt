package wallapp.utils

import android.content.Context
import android.view.WindowManager
import wallapp.math.geometry.Point
import wallapp.math.geometry.point
import kotlin.math.max
import kotlin.math.min
import android.graphics.Point as PointSystem

class WindowDimensSystem(private val context: Context) : WindowDimens {

    private val windowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val displayMetrics = context.resources.displayMetrics
    override val density: Float = displayMetrics.density

    /**
     * The real size of the display without subtracting any window decor such as
     * the status or nav bar.
     */
    override val displaySize: Point by lazy {
        PointSystem().apply {
            windowManager.defaultDisplay.getRealSize(this)
        }.point
    }
    override val maxDisplayDimension by lazy { max(displaySize.x, displaySize.y) }
    override val minDisplayDimension by lazy { min(displaySize.x, displaySize.y) }
}