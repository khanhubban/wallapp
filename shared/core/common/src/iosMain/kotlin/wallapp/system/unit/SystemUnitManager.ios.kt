package wallapp.system.unit

import androidx.compose.ui.unit.Dp
import platform.UIKit.UIScreen

class SystemUnitManagerIos : SystemUnitManager {

    private val scale: Float
        get() = UIScreen.mainScreen.scale.toFloat()

    override fun dpToPx(dp: Float): Float = dp * scale
    override fun dpToPx(dp: Int): Float = dpToPx(dp.toFloat())
    override fun dpToPx(dp: Dp): Float = dpToPx(dp.value)

    override fun pxToDp(px: Float): Dp = Dp(px / scale)
    override fun pxToDp(px: Int): Dp = pxToDp(px.toFloat())
}
