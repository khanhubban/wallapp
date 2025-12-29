package wallapp.system.unit

import androidx.compose.ui.unit.Dp
import java.awt.Toolkit

class SystemUnitManagerDesktop : SystemUnitManager {

    private val toolkit = Toolkit.getDefaultToolkit()
    private val screenResolution = toolkit.screenResolution
    private val scaleFactor = screenResolution / 160.0

    override fun dpToPx(dp: Float): Float = (dp * scaleFactor).toFloat()
    override fun dpToPx(dp: Int): Float = dpToPx(dp.toFloat())
    override fun dpToPx(dp: Dp): Float = dpToPx(dp.value)

    override fun pxToDp(px: Float): Dp = Dp((px / scaleFactor).toFloat())
    override fun pxToDp(px: Int): Dp = pxToDp(px.toFloat())
}
